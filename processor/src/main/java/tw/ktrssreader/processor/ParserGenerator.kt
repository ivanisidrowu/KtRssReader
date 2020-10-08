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
import tw.ktrssreader.annotation.OrderType
import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.*
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

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

    private var topLevelCandidateOrder = emptyArray<OrderType>()

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
        val rssTag = rootElement.getAnnotation(RssTag::class.java)
        val rssRawData = rootElement.getAnnotation(RssRawData::class.java)
        if (rssTag != null && rssRawData != null) {
            logger.error(
                "@RssTag and @RssRawData should not be used on the same class!",
                rootElement
            )
        }
        val tagName = rssTag?.name?.takeIfNotEmpty() ?: rootElement.simpleName.toString()
        val funSpec = FunSpec.builder(tagName.getFuncName())
            .receiver(xmlParserClass)
            .returns(outputClass)
        val propertyToParseData = mutableMapOf<String, ParseData>()
        topLevelCandidateOrder =
            rssTag?.order ?: arrayOf(OrderType.RSS_STANDARD, OrderType.ITUNES, OrderType.GOOGLE)

        val annotations = preProcessAnnotations(rootElement)
        rootElement.enclosedElements.forEach { preProcessParseData(it, propertyToParseData, annotations) }
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
            |return $outputClassName(
        """.trimMargin(),
            skipMemberName
        )
        // Generate constructor statements
        propertyToParseData.forEach {
            val stringBuilder = StringBuilder()
            it.value.tagCandidates.forEach { tag ->
                val variableName = it.key.getVariableName(tag)
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(variableName)
                } else {
                    stringBuilder.append(" ?: $variableName")
                }
            }
            funSpec.addStatement("\t${it.key} = $stringBuilder,")
        }

        funSpec.addStatement(")")
        return funSpec.build()
    }

    private fun preProcessAnnotations(rootElement: Element): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        rootElement.enclosedElements.forEach { child ->
            if (child.kind != ElementKind.METHOD
                || !child.simpleName.startsWith(GET_PREFIX)
                || !child.simpleName.contains(ANNOTATION_SIGN)
            ) return@forEach

            // Extract name which is started with 'get' (length = 3).
            // The child simple name example: getList$annotations
            val nameFromMethod = child.simpleName.substring(3)
                .decapitalize(Locale.ROOT)
                .substringBeforeLast('$')

            val rssTag: RssTag? = child.getAnnotation(RssTag::class.java)
            val rssAttribute: RssAttribute? = child.getAnnotation(RssAttribute::class.java)
            val rssRawData: RssRawData? = child.getAnnotation(RssRawData::class.java)
            val nonNullCount = listOf<Any?>(rssTag, rssAttribute, rssRawData).count { it != null }
            if (nonNullCount > 1) {
                logger.error("You can't annotate more than one annotation at a field or property!", child)
            }

            result[nameFromMethod] = rssTag ?: rssAttribute ?: rssRawData
        }
        return result
    }

    private fun preProcessParseData(
        child: Element,
        parseDataMap: MutableMap<String, ParseData>,
        nameToAnnotation: Map<String, Any?>
    ) {
        if (child.kind != ElementKind.METHOD
            || !child.simpleName.startsWith(GET_PREFIX)
            || child.simpleName.contains(ANNOTATION_SIGN)
        ) return

        // Extract name which is started with 'get' (length = 3).
        // The child simple name example: getList$annotations
        val nameFromMethod = child.simpleName.substring(3).decapitalize(Locale.ROOT)
        val exeElement = child as? ExecutableElement ?: return
        val rawType = exeElement.returnType.toString()
        val type: String?
        val packageName: String?
        val dataType: DataType
        var listItemType: String? = null

        if (rawType.isListType()) {
            listItemType = rawType.extractListType()
            type = rawType
                .substringBeforeLast('<')
                .extractType()
            packageName = rawType.substringAfterLast('<')
                .substringBeforeLast('>')
                .substringBeforeLast('.')
            dataType = DataType.LIST
        } else {
            type = rawType.extractType()
            dataType = when {
                nameToAnnotation[nameFromMethod] is RssAttribute -> DataType.ATTRIBUTE
                rawType.isPrimitive() -> DataType.PRIMITIVE
                else -> DataType.OTHER
            }
            packageName = rawType.substringBeforeLast('.')
        }

        parseDataMap[nameFromMethod] = ParseData(
            type = type,
            rawType = rawType,
            dataType = dataType,
            listItemType = listItemType,
            packageName = packageName,
            processorElement = child,
            tagCandidates = getTagCandidates(nameToAnnotation, nameFromMethod, child)
        )
    }

    private fun generateVariableStatement(
        propertyToParseData: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        val name = propertyToParseData.key
        val data = propertyToParseData.value
        val packageName = data.packageName ?: return
        val type = data.type ?: return

        when (data.dataType) {
            DataType.LIST -> {
                data.listItemType ?: return

                val itemClassName = ClassName(packageName, data.listItemType)
                data.tagCandidates.forEach { tag ->
                    funSpec.addStatement("var ${name.getVariableName(tag)}: ArrayList<%T> = arrayListOf()", itemClassName)
                }
            }
            DataType.PRIMITIVE -> {
                val kClass = type.getKPrimitiveClass() ?: return

                data.tagCandidates.forEach { tag ->
                    funSpec.addStatement("var ${name.getVariableName(tag)}: %T? = null", kClass)
                }
            }
            DataType.ATTRIBUTE -> {
                val kClass = type.getKPrimitiveClass() ?: return

                data.tagCandidates.forEach { tag ->
                    val statement =
                        "var ${name.getVariableName(tag)}: %1T? = getAttributeValue(null, \"$tag\")"
                            .appendTypeConversion(type)
                    if (type == Boolean::class.java.simpleName) {
                        funSpec.addStatement(statement, kClass, booleanConversionMemberName)
                    } else {
                        funSpec.addStatement(statement, kClass)
                    }
                }
            }
            else -> {
                val className = ClassName(packageName, type)
                data.tagCandidates.forEach { tag ->
                    funSpec.addStatement("var ${name.getVariableName(tag)}: %T? = null", className)
                }
            }
        }
    }

    private fun generateVariableAssignment(
        entry: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        val data = entry.value
        val name = entry.key
        when (data.dataType) {
            DataType.LIST -> {
                val listItemType = data.listItemType ?: return

                data.tagCandidates.forEach { tag ->
                    val memberName =
                        MemberName(listItemType.getGeneratedClassPath(), tag.getFuncName())
                    funSpec.addStatement("\t\t\"$tag\" -> ${name.getVariableName(tag)}.add(%M())", memberName)
                }
            }
            DataType.PRIMITIVE -> {
                val type = data.type ?: return

                data.tagCandidates.forEach { tag ->
                    funSpec.addPrimitiveStatement(tag, name.getVariableName(tag), type)
                }
            }
            DataType.ATTRIBUTE -> {
                // Do nothing.
            }
            else -> {
                val type = data.type ?: return

                data.tagCandidates.forEach { tag ->
                    val memberName = MemberName(type.getGeneratedClassPath(), tag.getFuncName())
                    funSpec.addStatement("\t\t\"$tag\" -> ${name.getVariableName(tag)} = %M()", memberName)
                }
            }
        }
    }

    private fun getTagCandidates(
        nameToAnnotation: Map<String, Any?>,
        nameFromMethod: String,
        element: Element
    ): List<String> {

        fun getTags(order: Array<OrderType>, tag: String): List<String> {
            return order.map { orderType ->
                when (orderType) {
                    OrderType.GOOGLE -> "$GOOGLE_PREFIX:$tag"
                    OrderType.ITUNES -> "$ITUNES_PREFIX:$tag"
                    else -> tag
                }
            }
        }

        return when (val annotation = nameToAnnotation[nameFromMethod]) {
            is RssTag -> {
                val tag = annotation.name.takeIfNotEmpty() ?: nameFromMethod
                getTags(annotation.order, tag)
            }
            is RssAttribute -> {
                val tag = annotation.name.takeIfNotEmpty() ?: nameFromMethod
                listOf(tag)
            }
            is RssRawData -> {
                if (annotation.rawTags.isEmpty()) {
                    logger.error("You have to put raw tags into @RssRawData!", element)
                }
                annotation.rawTags.toList()
            }
            else -> {
                getTags(topLevelCandidateOrder, nameFromMethod)
            }
        }
    }

    private fun String.getVariableName(tag: String): String {
        return when {
            tag.startsWith(GOOGLE_PREFIX) -> "$this${GOOGLE_PREFIX.capitalize(Locale.ROOT)}"
            tag.startsWith(ITUNES_PREFIX) -> "$this${ITUNES_PREFIX.capitalize(Locale.ROOT)}"
            else -> this
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