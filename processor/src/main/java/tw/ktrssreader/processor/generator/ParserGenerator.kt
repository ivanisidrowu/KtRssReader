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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toTypeName
import tw.ktrssreader.annotation.*
import tw.ktrssreader.processor.DataType
import tw.ktrssreader.processor.ParseData
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.*

abstract class ParserGenerator(protected val logger: KSPLogger) : Generator {

    abstract val outputClassName: ClassName

    protected val extensionFullPath = "$GENERATOR_PACKAGE.$EXTENSION_NAME"
    protected val booleanConversionMemberName = MemberName(extensionFullPath, METHOD_TO_BOOLEAN)

    protected var rootTagName: String? = null
    protected var hasRssValueAnnotation: Boolean = false
    protected var topLevelCandidateOrder = emptyArray<OrderType>()

    private val readStringMemberName = MemberName(extensionFullPath, METHOD_READ_STRING)

    @OptIn(KspExperimental::class)
    protected fun preProcessAnnotations(ksClassDeclaration: KSClassDeclaration): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        ksClassDeclaration.getDeclaredProperties().forEach { child ->
            val nameFromMethod = child.simpleName.asString().filterQuestionMark()

            val rssValue: RssValue? = child.getAnnotationsByType(RssValue::class).firstOrNull()
            val rssTag: RssTag? = child.getAnnotationsByType(RssTag::class).firstOrNull()
            val rssAttribute: RssAttribute? = child.getAnnotationsByType(RssAttribute::class).firstOrNull()
            val rssRawData: RssRawData? = child.getAnnotationsByType(RssRawData::class).firstOrNull()
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

    protected fun preProcessParseData(
        child: KSPropertyDeclaration,
        parseDataMap: MutableMap<String, ParseData>,
        nameToAnnotation: Map<String, Any?>
    ) {
        val nameFromMethod = child.simpleName.asString()
        val rawType = child.getter?.returnType?.toTypeName()?.toString()?.filterQuestionMark() ?: return
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
            declaration = child,
            tagCandidates = getTagCandidates(nameToAnnotation, nameFromMethod, child)
        )
    }

    protected fun generateVariableStatement(
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
                    if (type.isBooleanType()) {
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

    protected fun generateVariableAssignment(
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
                        val statement = "$TAB$TAB\"$tag\" -> %M(\"$tag\")"
                        funSpec.addPrimitiveStatement(statement, listItemType)
                        funSpec.addStatement("?.let { ${name.getVariableName(tag)}.add(it) }")
                    }
                } else {
                    data.tagCandidates.forEach { tag ->
                        val functionName = tag.getFuncName()
                        if (listItemType == outputClassName.simpleName) {
                            funSpec.addStatement("$TAB$TAB\"$tag\" -> ${name.getVariableName(tag)}.add($functionName())")
                        } else {
                            val memberName =
                                MemberName(listItemType.getGeneratedClassPath(), functionName)
                            funSpec.addStatement("$TAB$TAB\"$tag\" -> ${name.getVariableName(tag)}.add(%M())", memberName)
                        }
                    }
                }
            }
            DataType.PRIMITIVE -> {
                val type = data.type ?: return

                data.tagCandidates.forEach { tag ->
                    val variableName = name.getVariableName(tag)
                    funSpec.addPrimitiveStatement(
                        "$TAB$TAB\"$tag\" -> $variableName = %M(\"$tag\")",
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

    open fun generateConstructor(
        parseData: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        val stringBuilder = StringBuilder()
        val dataType = parseData.value.dataType
        val tags = parseData.value.tagCandidates

        if (dataType == DataType.VALUE) {
            val name = parseData.key
            val type = parseData.value.type ?: return
            val statement = "$TAB$TAB$name = %M(\"$rootTagName\"),".appendTypeConversion(type)
            if (type.isBooleanType()) {
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
        declaration: KSPropertyDeclaration
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
                    logger.error("You have to put raw tags into @RssRawData!", declaration)
                }
                annotation.rawTags.toList()
            }
            else -> {
                getTags(topLevelCandidateOrder, nameFromMethod)
            }
        }
    }

    protected fun FunSpec.Builder.addPrimitiveStatement(
        readStringStatement: String,
        typeString: String
    ) {
        val statement = readStringStatement.appendTypeConversion(typeString)
        if (typeString.isBooleanType()) {
            addStatement(statement, readStringMemberName, booleanConversionMemberName)
        } else {
            addStatement(statement, readStringMemberName)
        }
    }
}
