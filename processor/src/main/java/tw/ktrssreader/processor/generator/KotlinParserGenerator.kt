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
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import tw.ktrssreader.annotation.OrderType
import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.processor.DataType
import tw.ktrssreader.processor.ParseData
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.*

class KotlinParserGenerator(
    private val declaration: KSClassDeclaration,
    private val isRoot: Boolean,
    logger: KSPLogger
) : ParserGenerator(logger) {

    override val outputClassName = ClassName(declaration.packageName.asString(), declaration.simpleName.asString())

    private val exceptionClass = ClassName("java.lang", "IllegalStateException")
    private val docBuilderFactoryClass = ClassName("javax.xml.parsers", "DocumentBuilderFactory")
    private val elementClassName = ClassName("org.w3c.dom", "Element")
    private val listClassName = ClassName("java.util", "ArrayList")
    private val getAttributeOrNullMemberName = MemberName(extensionFullPath, METHOD_GET_ATTR_OR_NULL)
    private val getElementByTagMemberName = MemberName(extensionFullPath, METHOD_GET_ELEMENT_BY_TAG)

    override fun generate(): FileSpec {
        val generatedClassName = "${declaration.simpleName.asString()}$PARSER_SUFFIX"
        return FileSpec.builder(GENERATOR_PACKAGE, generatedClassName)
            .addType(getObjectTypeSpec(generatedClassName))
            .build()
    }

    private fun getObjectTypeSpec(className: String): TypeSpec {
        val builder = TypeSpec.objectBuilder(className)
        val outputClassName = declaration.simpleName.asString()

        if (isRoot) {
            builder.addFunction(getParseFuncSpec())
        }
        return builder
            .addFunction(getClassFunSpec(declaration, outputClassName, builder))
            .build()
    }

    private fun getParseFuncSpec(): FunSpec {
        return FunSpec.builder(PARSER_FUNC_NAME)
            .addParameter("xml", String::class)
            .addCode(
                """
                | val builder = %4T.newInstance().newDocumentBuilder()
                | val document = builder.parse(xml.byteInputStream())
                | document.documentElement.normalize()
                | val nodeList = document.getElementsByTagName("%2L")
                | var result: %1T? = null
                |
                | if (nodeList?.length == 1) {
                |${TAB}${TAB}val element = nodeList.item(0) as? Element
                |${TAB}${TAB}element?.let {
                |${TAB}${TAB}${TAB}result = it.getChannel()
                |${TAB}$TAB}
                | }
                | return result ?: throw %3T("No valid channel tag in the RSS feed.")
                | """.trimMargin(),
                outputClassName, CHANNEL, exceptionClass, docBuilderFactoryClass
            )
            .returns(outputClassName)
            .build()
    }

    @OptIn(KspExperimental::class)
    private fun getClassFunSpec(
        classDeclaration: KSClassDeclaration,
        outputClassName: String,
        objectBuilder: TypeSpec.Builder
    ): FunSpec {
        val outputClass = ClassName(classDeclaration.packageName.asString(), outputClassName)
        val rssTag = classDeclaration.getAnnotationsByType(RssTag::class).firstOrNull()
        val rssRawData = classDeclaration.getAnnotationsByType(RssRawData::class).firstOrNull()
        if (rssTag != null && rssRawData != null) {
            logger.error("@RssTag and @RssRawData should not be used on the same class!", classDeclaration)
        }

        val tagName = rssTag?.name?.takeIfNotEmpty() ?: classDeclaration.simpleName.asString()
        rootTagName = tagName
        logger.info("[KotlinParserGenerator][getActionFunSpec] $rootTagName")
        val funSpec = FunSpec.builder(tagName.getFuncName())
            .receiver(elementClassName)
            .returns(outputClass)
        val propertyToParseData = mutableMapOf<String, ParseData>()
        topLevelCandidateOrder =
            rssTag?.order ?: arrayOf(OrderType.RSS_STANDARD, OrderType.ITUNES, OrderType.GOOGLE)

        val annotations = preProcessAnnotations(classDeclaration)

        classDeclaration.getDeclaredProperties().forEach { preProcessParseData(it, propertyToParseData, annotations) }

        propertyToParseData.forEach { generateValueStatementForConstructor(it, funSpec, objectBuilder) }

        funSpec.addStatement("\nreturn $outputClassName(")
        propertyToParseData.forEach {
            generateConstructor(it, funSpec)
        }
        funSpec.addStatement("$TAB)")
        return funSpec.build()
    }

    private fun generateValueStatementForConstructor(
        propertyToParseData: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder,
        objectBuilder: TypeSpec.Builder
    ) {
        val name = propertyToParseData.key
        val data = propertyToParseData.value
        val packageName = data.packageName ?: return
        val type = data.type ?: return

        when (data.dataType) {
            DataType.LIST -> {
                generateValueListStatement(data, objectBuilder, packageName, funSpec, name)
            }
            DataType.PRIMITIVE -> {
                val kClass = type.getKPrimitiveClass() ?: return

                data.tagCandidates.forEach { tag ->
                    funSpec.addStatement("val ${name.getVariableName(tag)}: %T? =", kClass)
                    funSpec.addPrimitiveStatement("%M(\"$tag\")", type)
                }
            }
            DataType.ATTRIBUTE -> {
                generateValueAttributeStatement(type, data, name, funSpec)
            }
            DataType.VALUE -> {
                // Do nothing
            }
            else -> {
                val className = ClassName(packageName, type)
                data.tagCandidates.forEach { tag ->
                    val memberName = MemberName(type.getGeneratedClassPath(), tag.getFuncName())
                    funSpec.addStatement(
                        "val ${name.getVariableName(tag)}: %T? = %M(\"$tag\")?.%M()",
                        className, getElementByTagMemberName, memberName
                    )
                }
            }
        }
    }

    private fun generateValueAttributeStatement(
        type: String,
        data: ParseData,
        name: String,
        funSpec: FunSpec.Builder
    ) {
        val kClass = type.getKPrimitiveClass() ?: return

        data.tagCandidates.forEach { tag ->
            val statement =
                "val ${name.getVariableName(tag)}: %T? = %M(\"$tag\")"
                    .appendTypeConversion(type)
            if (type.isBooleanType()) {
                funSpec.addStatement(
                    statement,
                    kClass,
                    getAttributeOrNullMemberName,
                    booleanConversionMemberName
                )
            } else {
                funSpec.addStatement(statement, kClass, getAttributeOrNullMemberName)
            }
        }
    }

    private fun generateValueListStatement(
        data: ParseData,
        objectBuilder: TypeSpec.Builder,
        packageName: String,
        funSpec: FunSpec.Builder,
        name: String
    ) {
        val itemType = data.listItemType ?: return

        if (itemType.isPrimitive()) {
            val kClass = itemType.getKPrimitiveClass() ?: return

            data.tagCandidates.forEach { tag ->
                objectBuilder.addFunction(generateListFunction(tag, itemType, packageName))
                funSpec.addStatement(
                    "val ${name.getVariableName(tag)}: ArrayList<%T> = ${tag.getListFuncName()}()",
                    kClass
                )
            }
        } else {
            val itemClassName = ClassName(packageName, data.listItemType)
            data.tagCandidates.forEach { tag ->
                objectBuilder.addFunction(generateListFunction(tag, itemType, packageName))
                funSpec.addStatement(
                    "val ${name.getVariableName(tag)}: ArrayList<%T> = ${tag.getListFuncName()}()",
                    itemClassName
                )
            }
        }
    }

    override fun generateConstructor(
        parseData: Map.Entry<String, ParseData>,
        funSpec: FunSpec.Builder
    ) {
        if (parseData.value.dataType == DataType.VALUE) {
            val name = parseData.key
            val type = parseData.value.type ?: return
            val statement = "$TAB$TAB$name = textContent?.takeIf { it.isNotEmpty() }".appendTypeConversion(type)
            if (type.isBooleanType()) {
                funSpec.addStatement(statement, booleanConversionMemberName)
            } else {
                funSpec.addStatement(statement)
            }
            return
        }

        super.generateConstructor(parseData, funSpec)
    }

    private fun generateListFunction(tag: String, itemType: String, packageName: String): FunSpec {
        val funSpec = FunSpec.builder(tag.getListFuncName())
            .receiver(elementClassName)
            .addModifiers(KModifier.PRIVATE)
        logger.info("[KotlinParserGenerator][generateListFunction] $rootTagName, $tag, $itemType, $packageName")
        val codeBlock =
            """
            |val result: ArrayList<%T> = arrayListOf()
            |val nodeList = getElementsByTagName("$tag")
            |val listLength = nodeList?.length ?: 0
            |for (i in 0 until listLength) {
            |${TAB}val element = nodeList.item(i) as? Element ?: continue
            |${TAB}val parent = element.parentNode as? Element
            |${TAB}if (parent?.tagName == "$rootTagName" || parent?.tagName == "$tag") {
            """.trimMargin()
        if (itemType.isPrimitive()) {
            val kClass = itemType.getKPrimitiveClass()

            kClass?.let {
                funSpec.returns(listClassName.parameterizedBy(it.asTypeName()))
                funSpec.addCode(codeBlock, it)
                funSpec.addStatement("\n")
                if (itemType.isTypeString()) {
                    funSpec.addStatement("${TAB}${TAB}element.textContent")
                } else {
                    val statement = "${TAB}${TAB}element.%M(\"$tag\", \"$rootTagName\")"
                    funSpec.addPrimitiveStatement(statement, itemType)
                }
                funSpec.addStatement("${TAB}${TAB}$TAB?.let { result.add(it) }")
            }
        } else {
            val itemClassName = ClassName(packageName, itemType)
            funSpec.returns(listClassName.parameterizedBy(itemClassName))
            funSpec.addCode(codeBlock, itemClassName)
            funSpec.addStatement("\n")
            if (itemType == outputClassName.simpleName) {
                funSpec.addStatement("${TAB}element.${tag.getFuncName()}()?.let { result.add(it) }")
            } else {
                val memberName = MemberName(itemType.getGeneratedClassPath(), tag.getFuncName())
                funSpec.addStatement("${TAB}element.%M()?.let { result.add(it) }", memberName)
            }
        }
        funSpec.addCode(
            """
            |$TAB}
            |}
            |return result
        """.trimMargin()
        )
        return funSpec.build()
    }
}
