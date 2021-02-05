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
import tw.ktrssreader.processor.ParseData
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.*
import javax.lang.model.element.Element

class AndroidParserGenerator(
    private val element: Element,
    private val isRoot: Boolean,
    logger: Logger
) : ParserGenerator(logger) {

    private val outputClass = ClassName(element.getPackage(), element.simpleName.toString())
    private val xmlParserClass = ClassName(XML_PULL_PACKAGE, XML_PULL_NAME)
    private val xmlParserExceptionClass = ClassName(XML_PULL_PACKAGE, XML_PULL_EXCEPTION_NAME)
    private val skipMemberName = MemberName(extensionFullPath, METHOD_SKIP)
    private val getParserMemberName = MemberName(extensionFullPath, METHOD_GET_PARSER)

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
        var index = 0
        val lastIndex = propertyToParseData.size - 1
        propertyToParseData.forEach {
            generateConstructor(it, funSpec, index == lastIndex)
            index ++
        }

        funSpec.addStatement("$TAB)")
        return funSpec.build()
    }
}