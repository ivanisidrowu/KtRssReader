/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tw.ktrssreader.processor

import com.squareup.kotlinpoet.*
import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.*
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class ParserGenerator(
    private val element: Element,
    private val isRoot: Boolean,
    private val logger: Logger
) : Generator {

    private val outputClass = ClassName(element.getPackage(), element.simpleName.toString())
    private val xmlParserClass = ClassName(XML_PULL_PACKAGE, XML_PULL_NAME)
    private val xmlParserExceptionClass = ClassName(XML_PULL_PACKAGE, XML_PULL_EXCEPTION_NAME)
    private val extensionFullPath = "$GENERATOR_PACKAGE.$EXTENSION_NAME"
    private val skipMemberName = MemberName(extensionFullPath, METHOD_SKIP)
    private val readStringMemberName = MemberName(extensionFullPath, METHOD_READ_STRING)
    private val getParserMemberName = MemberName(extensionFullPath, METHOD_GET_PARSER)
    private val booleanConversionMemberName = MemberName(extensionFullPath, METHOD_TO_BOOLEAN)

    override fun generate(): FileSpec {
        val generatedClassName = "${element.simpleName}$PARSER_SUFFIX"
        return FileSpec.builder(GENERATOR_PACKAGE, generatedClassName)
            .addType(getObjectTypeSpec(generatedClassName))
            .build()
    }

    private fun getObjectTypeSpec(className: String): TypeSpec {
        val builder = TypeSpec.objectBuilder(className)
        val outputClassName = element.simpleName.toString()

        if (isRoot) {
            builder.addFunction(getParseFuncSpec())
        }
        return builder
            .addFunction(getClassFunSpec(element, outputClassName))
            .build()
    }

    private fun getParseFuncSpec(): FunSpec {
        return FunSpec.builder(PARSER_FUNC_NAME)
            .addParameter("xml", String::class)
            .addCode(
                """
                |val parser = %5M(xml)
                |
                |var result: %1T? = null
                |while (parser.next() != XmlPullParser.END_TAG) {
                |    if (parser.eventType != XmlPullParser.START_TAG) continue
                |
                |    if (parser.name == "%2L") {
                |        result = parser.getChannel()
                |        break
                |    } else {
                |        parser.%4M()
                |    }
                |}
                |return result ?: throw %3T("No valid channel tag in the RSS feed.")
                """.trimMargin(),
                outputClass, CHANNEL, xmlParserExceptionClass, skipMemberName, getParserMemberName
            )
            .returns(outputClass)
            .build()
    }

    private fun getClassFunSpec(rootElement: Element, outputClassName: String): FunSpec {
        val outputClass = ClassName(rootElement.getPackage(), outputClassName)
        val annotation = rootElement.getAnnotation(RssTag::class.java)
        val tagName = annotation?.name?.takeIfNotEmpty() ?: rootElement.simpleName.toString()
        val funSpec = FunSpec.builder(tagName.getFuncName())
            .receiver(xmlParserClass)
            .returns(outputClass)
        val propertyToParseData = mutableMapOf<String, ParseData>()

        rootElement.enclosedElements.forEach {
            preProcessParseData(child = it, parseDataMap = propertyToParseData)
        }
        funSpec.addStatement("require(XmlPullParser.START_TAG, null, \"$tagName\")")
        propertyToParseData.forEach { generateVariableStatement(it, funSpec) }
        funSpec.addCode(
            """
            |while (next() != XmlPullParser.END_TAG) {
            |if (eventType != XmlPullParser.START_TAG) continue
            |
            |when (this.name) {
            |
        """.trimMargin()
        )
        propertyToParseData.forEach { generateVariableAssignment(it, funSpec) }
        funSpec.addCode(
            """
            |       else -> %M()
            |  }
            |}
            |require(XmlPullParser.END_TAG, null, "$tagName")
            |return $outputClassName(
        """.trimMargin(),
            skipMemberName
        )
        // Generate constructor statements
        propertyToParseData.forEach { funSpec.addStatement("${it.key} = ${it.key},") }

        funSpec.addStatement(")")
        return funSpec.build()
    }

    private fun preProcessParseData(
        child: Element,
        parseDataMap: MutableMap<String, ParseData>
    ) {
        if (child.kind == ElementKind.METHOD
            && child.simpleName.contains(GET_PREFIX)
            && child.simpleName.contains(ANNOTATION_SIGN)
        ) {
            // Extract name which is started with 'get' (length = 3).
            // The child simple name example: getList$annotations
            val nameFromMethod = child.simpleName.substring(3)
                .decapitalize(Locale.ROOT)
                .substringBeforeLast('$')
            if (parseDataMap.containsKey(nameFromMethod)) {
                val rssTag = child.getAnnotation(RssTag::class.java)
                val rssAttribute = child.getAnnotation(RssAttribute::class.java)
                val tag = rssAttribute?.name?.takeIfNotEmpty()
                    ?: rssTag?.name?.takeIfNotEmpty()
                    ?: nameFromMethod
                val dataType = if (rssAttribute == null) DataType.LIST else DataType.ATTRIBUTE
                val clone = parseDataMap[nameFromMethod]
                    ?.copy(tag = tag, dataType = dataType) ?: return

                parseDataMap[nameFromMethod] = clone
            }
        }

        if (child.kind != ElementKind.FIELD) return

        val name = child.simpleName.toString()
        val rawType = child.asType()?.toString()
        val tag = parseDataMap[name]?.tag ?: name
        val type: String?
        val packageName: String?
        var dataType = parseDataMap[name]?.dataType
        var listItemType: String? = null

        if (rawType?.isListType() == true) {
            listItemType = rawType.extractListType()
            type = rawType
                .substringBeforeLast('<')
                .extractType()
            packageName = rawType.substringAfterLast('<')
                .substringBeforeLast('>')
                .substringBeforeLast('.')
            dataType = DataType.LIST
        } else {
            type = rawType?.extractType()
            if (dataType == null) {
                dataType =
                    if (rawType?.isPrimitive() == true) DataType.PRIMITIVE else DataType.OTHER
            }
            packageName = rawType?.substringBeforeLast('.')
        }
        parseDataMap[name] = ParseData(
            tag = tag,
            type = type,
            rawType = rawType,
            dataType = dataType,
            listItemType = listItemType,
            packageName = packageName,
            processorElement = child,
        )
    }

    private fun generateVariableAssignment(
        entry: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        val data = entry.value
        val tag = data.tag
        val name = entry.key
        when (data.dataType) {
            DataType.LIST -> {
                val listItemType = data.listItemType ?: return

                val memberName = MemberName(listItemType.getGeneratedClassPath(), tag.getFuncName())
                funSpec.addStatement("\t\t\"$tag\" -> $name.add(%M())", memberName)
            }
            DataType.PRIMITIVE -> {
                val type = data.type ?: return
                funSpec.addPrimitiveStatement(tagName = tag, variableName = name, typeString = type)
            }
            DataType.ATTRIBUTE -> {
                // Do nothing.
            }
            else -> {
                val type = data.type ?: return
                val memberName = MemberName(type.getGeneratedClassPath(), tag.getFuncName())
                funSpec.addStatement("\t\t\"$tag\" -> $name = %M()", memberName)
            }
        }
    }

    private fun generateVariableStatement(
        propertyToParseData: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        val name = propertyToParseData.key
        val data = propertyToParseData.value
        val packageName = data.packageName ?: return

        if (data.dataType == DataType.LIST) {
            data.listItemType ?: return

            val itemClassName = ClassName(packageName, data.listItemType)
            funSpec.addStatement("var ${name}: ArrayList<%T> = arrayListOf()", itemClassName)
        } else {
            val type = data.type ?: return

            when (data.dataType) {
                DataType.PRIMITIVE -> {
                    val kClass = type.getKPrimitiveClass() ?: return

                    funSpec.addStatement("var ${name}: %T? = null", kClass)
                }
                DataType.ATTRIBUTE -> {
                    val kClass = type.getKPrimitiveClass() ?: return

                    val statement = "var ${name}: %1T? = getAttributeValue(null, \"${data.tag}\")"
                        .appendTypeConversion(type)
                    if (type == Boolean::class.java.simpleName) {
                        funSpec.addStatement(statement, kClass, booleanConversionMemberName)
                    } else {
                        funSpec.addStatement(statement, kClass)
                    }
                }
                else -> {
                    val className = ClassName(packageName, type)
                    funSpec.addStatement("var ${name}: %T? = null", className)
                }
            }
        }
    }

    private fun FunSpec.Builder.addPrimitiveStatement(
        tagName: String,
        variableName: String,
        typeString: String,
    ) {
        val readStringStatement = "\t\t\"$tagName\" -> $variableName = %1M(\"$tagName\")"
        val statement = readStringStatement.appendTypeConversion(typeString)
        if (typeString == Boolean::class.java.simpleName) {
            addStatement(statement, readStringMemberName, booleanConversionMemberName)
        } else {
            addStatement(statement, readStringMemberName)
        }
    }

    private fun String.appendTypeConversion(typeString: String): String {
        return when (typeString) {
            String::class.java.simpleName -> this
            Integer::class.java.simpleName -> "$this.toIntOrNull()"
            Boolean::class.java.simpleName -> "$this.%2M()"
            Long::class.java.simpleName -> "$this.toLongOrNull()"
            Short::class.java.simpleName -> "$this.toShortOrNull()"
            else -> this
        }
    }
}