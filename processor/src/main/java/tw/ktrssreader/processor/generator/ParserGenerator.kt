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

package tw.ktrssreader.processor.generator

import com.squareup.kotlinpoet.*
import tw.ktrssreader.annotation.*
import tw.ktrssreader.processor.DataType
import tw.ktrssreader.processor.ParseData
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.*
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
    private var rootTagName: String? = null
    private var hasRssValueAnnotation = false

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
                |${TAB}if (parser.eventType != XmlPullParser.START_TAG) continue
                |
                |${TAB}if (parser.name == "%2L") {
                |${TAB}${TAB}result = parser.getChannel()
                |${TAB}${TAB}break
                |${TAB}} else {
                |${TAB}${TAB}parser.%4M()
                |${TAB}}
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
        rootTagName = tagName
        val funSpec = FunSpec.builder(tagName.getFuncName())
            .receiver(xmlParserClass)
            .returns(outputClass)
        val propertyToParseData = mutableMapOf<String, ParseData>()
        topLevelCandidateOrder =
            rssTag?.order ?: arrayOf(OrderType.RSS_STANDARD, OrderType.ITUNES, OrderType.GOOGLE)

        val annotations = preProcessAnnotations(rootElement)
        rootElement.enclosedElements.forEach { preProcessParseData(it, propertyToParseData, annotations) }
        propertyToParseData.forEach { generateVariableStatement(it, funSpec) }

        if (!hasRssValueAnnotation) {
            funSpec.addCode(
                """
            |while (next() != XmlPullParser.END_TAG) {
            |${TAB}if (eventType != XmlPullParser.START_TAG) continue
            |
            |${TAB}when (this.name) {
            |
            """.trimMargin()
            )
            propertyToParseData.forEach { generateVariableAssignment(it, funSpec) }
            funSpec.addCode(
                """
            |${TAB}${TAB}else -> %M()
            |${TAB}}
            |}
        """.trimMargin(),
                skipMemberName
            )
        }

        funSpec.addStatement("\nreturn $outputClassName(")
        // Generate constructor statements
        propertyToParseData.forEach { generateConstructor(it, funSpec) }

        funSpec.addStatement("$TAB)")
        return funSpec.build()
    }

    private fun preProcessAnnotations(rootElement: Element): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        rootElement.enclosedElements.forEach { child ->
            if (child.kind != ElementKind.METHOD
                || !child.simpleName.isGetterMethod()
                || !child.simpleName.contains(ANNOTATION_SIGN)
            ) return@forEach

            val nameFromMethod = child.simpleName.extractNameFromMethod()

            val rssValue: RssValue? = child.getAnnotation(RssValue::class.java)
            val rssTag: RssTag? = child.getAnnotation(RssTag::class.java)
            val rssAttribute: RssAttribute? = child.getAnnotation(RssAttribute::class.java)
            val rssRawData: RssRawData? = child.getAnnotation(RssRawData::class.java)
            val nonNullCount = listOf<Any?>(rssTag, rssAttribute, rssRawData).count { it != null }
            val attributeValueCount = listOf<Any?>(rssAttribute, rssValue).count { it != null }
            if (nonNullCount > 1 || attributeValueCount > 1) {
                logger.error(
                    "You can't annotate more than one annotation at a field or property!",
                    child
                )
            }

            result[nameFromMethod] = rssValue ?: rssTag ?: rssAttribute ?: rssRawData

            if (rssValue != null) {
                hasRssValueAnnotation = true
            }
        }
        return result
    }

    private fun preProcessParseData(
        child: Element,
        parseDataMap: MutableMap<String, ParseData>,
        nameToAnnotation: Map<String, Any?>
    ) {
        if (child.kind != ElementKind.METHOD
            || !child.simpleName.isGetterMethod()
            || child.simpleName.contains(ANNOTATION_SIGN)
        ) return

        val nameFromMethod = child.simpleName.extractNameFromMethod()
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
            val annotation = nameToAnnotation[nameFromMethod]
            dataType = when {
                annotation is RssValue -> DataType.VALUE
                annotation is RssAttribute -> DataType.ATTRIBUTE
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
                val itemType = data.listItemType ?: return

                if (itemType.isPrimitive()) {
                    val kClass = itemType.getKPrimitiveClass() ?: return

                    data.tagCandidates.forEach { tag ->
                        funSpec.addStatement(
                            "var ${name.getVariableName(tag)}: ArrayList<%T> = arrayListOf()",
                            kClass
                        )
                    }
                } else {
                    val itemClassName = ClassName(packageName, data.listItemType)
                    data.tagCandidates.forEach { tag ->
                        funSpec.addStatement(
                            "var ${name.getVariableName(tag)}: ArrayList<%T> = arrayListOf()",
                            itemClassName
                        )
                    }
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
                        "var ${name.getVariableName(tag)}: %T? = getAttributeValue(null, \"$tag\")"
                            .appendTypeConversion(type)
                    if (type.equals(Boolean::class.java.simpleName, ignoreCase = true)) {
                        funSpec.addStatement(statement, kClass, booleanConversionMemberName)
                    } else {
                        funSpec.addStatement(statement, kClass)
                    }
                }
            }
            DataType.VALUE -> {
                // Do nothing
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

                if (listItemType.isPrimitive()) {
                    data.tagCandidates.forEach { tag ->
                        val statement = "$TAB$TAB\"$tag\" -> %1M(\"$tag\")"
                        funSpec.addPrimitiveStatement(statement, listItemType)
                        funSpec.addStatement("?.let { ${name.getVariableName(tag)}.add(it) }")
                    }
                } else {
                    data.tagCandidates.forEach { tag ->
                        val memberName =
                            MemberName(listItemType.getGeneratedClassPath(), tag.getFuncName())
                        funSpec.addStatement("$TAB$TAB\"$tag\" -> ${name.getVariableName(tag)}.add(%M())", memberName)
                    }
                }
            }
            DataType.PRIMITIVE -> {
                val type = data.type ?: return

                data.tagCandidates.forEach { tag ->
                    val variableName = name.getVariableName(tag)
                    funSpec.addPrimitiveStatement(
                        "$TAB$TAB\"$tag\" -> $variableName = %1M(\"$tag\")",
                        type
                    )
                }
            }
            DataType.ATTRIBUTE, DataType.VALUE -> {
                // Do nothing.
            }
            else -> {
                val type = data.type ?: return

                data.tagCandidates.forEach { tag ->
                    val memberName = MemberName(type.getGeneratedClassPath(), tag.getFuncName())
                    funSpec.addStatement(
                        "$TAB$TAB\"$tag\" -> ${name.getVariableName(tag)} = %M()",
                        memberName
                    )
                }
            }
        }
    }

    private fun generateConstructor(
        parseData: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        val stringBuilder = StringBuilder()
        val dataType = parseData.value.dataType
        val tags = parseData.value.tagCandidates

        if (dataType == DataType.VALUE) {
            val name = parseData.key
            val type = parseData.value.type ?: return
            val statement = "$TAB$TAB$name = %M(\"$rootTagName\")".appendTypeConversion(type)
            if (type.equals(Boolean::class.java.simpleName, ignoreCase = true)) {
                funSpec.addStatement(statement, readStringMemberName, booleanConversionMemberName)
            } else {
                funSpec.addStatement(statement, readStringMemberName)
            }
            return
        }

        tags.forEachIndexed { index, tag ->
            val variableName = parseData.key.getVariableName(tag)
            if (stringBuilder.isEmpty()) {
                if (dataType == DataType.LIST) {
                    stringBuilder.append("$variableName.takeIf { it.isNotEmpty() }")
                } else {
                    stringBuilder.append(variableName)
                }
            } else {
                if (dataType == DataType.LIST && index != tags.lastIndex) {
                    stringBuilder.append(" ?: $variableName.takeIf { it.isNotEmpty() }")
                } else {
                    stringBuilder.append(" ?: $variableName")
                }
            }
        }
        funSpec.addStatement("$TAB$TAB${parseData.key} = $stringBuilder,")
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
            is RssValue -> {
                listOf(nameFromMethod)
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

    private fun FunSpec.Builder.addPrimitiveStatement(
        readStringStatement: String,
        typeString: String
    ) {
        val statement = readStringStatement.appendTypeConversion(typeString)
        if (typeString == Boolean::class.java.simpleName) {
            addStatement(statement, readStringMemberName, booleanConversionMemberName)
        } else {
            addStatement(statement, readStringMemberName)
        }
    }
}