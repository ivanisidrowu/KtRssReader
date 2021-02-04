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
import tw.ktrssreader.processor.const.*
import tw.ktrssreader.processor.util.Logger

class AndroidExtensionGenerator(
    private val logger: Logger
): ExtensionGenerator() {

    private val xmlParserClass = ClassName(XML_PULL_PACKAGE, XML_PULL_NAME)

    override fun generate(): FileSpec {
        logger.log("Generating $EXTENSION_NAME for Android.")
        return FileSpec.builder(GENERATOR_PACKAGE, EXTENSION_NAME)
            .addType(
                TypeSpec.objectBuilder(EXTENSION_NAME)
                    .addFunction(getXmlParserFunSpec())
                    .addFunction(getReadStringFunSpec())
                    .addFunction(getSkipFunSpec())
                    .addFunction(getBooleanConversionFunSpec())
                    .build()
            )
            .build()
    }

    private fun getXmlParserFunSpec(): FunSpec {
        val byteArrayStream = ClassName("java.io", "ByteArrayInputStream")
        val xmlUtil = ClassName("android.util", "Xml")

        return FunSpec.builder(METHOD_GET_PARSER)
            .addParameter("xml", String::class)
            .addCode(
                """
                |
                |%1T(xml.toByteArray()).use { inputStream ->
                |${TAB}return %2T.newPullParser().apply {
                |${TAB}${TAB}setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                |${TAB}${TAB}setInput(inputStream, null)
                |${TAB}${TAB}nextTag()
                |${TAB}}
                |}
                |
                """.trimMargin(),
                byteArrayStream, xmlUtil
            )
            .returns(xmlParserClass)
            .build()
    }

    private fun getReadStringFunSpec() = FunSpec.builder(METHOD_READ_STRING)
        .receiver(xmlParserClass)
        .addParameter("tagName", String::class)
        .addCode(
            """
            |require(XmlPullParser.START_TAG, null, tagName)
            |var content: String? = null
            |if (next() == XmlPullParser.TEXT) {
            |content = text
            |nextTag()
            |${TAB}if (eventType != XmlPullParser.END_TAG) {
            |${TAB}${TAB}skip()
            |${TAB}${TAB}nextTag()
            |${TAB}${TAB}content = null
            |${TAB}}
            |}
            |require(XmlPullParser.END_TAG, null, tagName)
            |return content
            """.trimMargin()
        )
        .returns(String::class.asTypeName().copy(nullable = true))
        .build()

    private fun getSkipFunSpec() = FunSpec.builder(METHOD_SKIP)
        .receiver(xmlParserClass)
        .addCode(
            """
            |if (eventType != XmlPullParser.START_TAG) {
            |${TAB}throw IllegalStateException()
            |}
            |var depth = 1
            |while (depth != 0) {
            |${TAB}when (next()) {
            |${TAB}${TAB}XmlPullParser.END_TAG -> depth--
            |${TAB}${TAB}XmlPullParser.START_TAG -> depth++
            |${TAB}}
            |}
            """.trimMargin()
        )
        .build()
}