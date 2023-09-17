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
import tw.ktrssreader.annotation.OrderType
import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.processor.ParseData
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.getFuncName
import tw.ktrssreader.processor.util.takeIfNotEmpty

class AndroidParserGenerator(
    private val classDeclaration: KSClassDeclaration,
    private val isRoot: Boolean,
    logger: KSPLogger
) : ParserGenerator(logger) {

    override val outputClassName = ClassName(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())

    private val xmlParserClass = ClassName(XML_PULL_PACKAGE, XML_PULL_NAME)
    private val xmlParserExceptionClass = ClassName(XML_PULL_PACKAGE, XML_PULL_EXCEPTION_NAME)
    private val skipMemberName = MemberName(extensionFullPath, METHOD_SKIP)
    private val getParserMemberName = MemberName(extensionFullPath, METHOD_GET_PARSER)

    override fun generate(): FileSpec {
        val generatedClassName = "${classDeclaration.simpleName.asString()}$PARSER_SUFFIX"
        return FileSpec.builder(GENERATOR_PACKAGE, generatedClassName)
            .addType(getObjectTypeSpec(generatedClassName))
            .build()
    }

    private fun getObjectTypeSpec(className: String): TypeSpec {
        val builder = TypeSpec.objectBuilder(className)
        val outputClassName = classDeclaration.simpleName.asString()

        if (isRoot) {
            builder.addFunction(getParseFuncSpec())
        }
        return builder
            .addFunction(getClassFunSpec(classDeclaration, outputClassName))
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
                |$TAB} else {
                |${TAB}${TAB}parser.%4M()
                |$TAB}
                |}
                |return result ?: throw %3T("No valid channel tag in the RSS feed.")
                """.trimMargin(),
                outputClassName, CHANNEL, xmlParserExceptionClass, skipMemberName, getParserMemberName
            )
            .returns(outputClassName)
            .build()
    }

    @OptIn(KspExperimental::class)
    private fun getClassFunSpec(classDeclaration: KSClassDeclaration, outputClassName: String): FunSpec {
        val outputClass = ClassName(classDeclaration.packageName.asString(), outputClassName)
        val rssTag = classDeclaration.getAnnotationsByType(RssTag::class).firstOrNull()
        val rssRawData = classDeclaration.getAnnotationsByType(RssRawData::class).firstOrNull()
        if (rssTag != null && rssRawData != null) {
            logger.error(
                "@RssTag and @RssRawData should not be used on the same class!",
                classDeclaration
            )
        }
        val tagName = rssTag?.name?.takeIfNotEmpty() ?: classDeclaration.simpleName.asString()
        rootTagName = tagName
        val funSpec = FunSpec.builder(tagName.getFuncName())
            .receiver(xmlParserClass)
            .returns(outputClass)
        val propertyToParseData = mutableMapOf<String, ParseData>()
        topLevelCandidateOrder =
            rssTag?.order ?: arrayOf(OrderType.RSS_STANDARD, OrderType.ITUNES, OrderType.GOOGLE)

        val annotations = preProcessAnnotations(classDeclaration)
        classDeclaration.getDeclaredProperties().forEach { preProcessParseData(it, propertyToParseData, annotations) }
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
            |$TAB}
            |}
        """.trimMargin(),
                skipMemberName
            )
        }

        funSpec.addStatement("\nreturn $outputClassName(")
        propertyToParseData.forEach {
            generateConstructor(it, funSpec)
        }

        funSpec.addStatement("$TAB)")
        return funSpec.build()
    }
}
