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

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import org.w3c.dom.Element
import tw.ktrssreader.processor.const.*

class KotlinExtensionGenerator(
    private val logger: KSPLogger
) : ExtensionGenerator() {

    private val elementClass = ClassName("org.w3c.dom", "Element")

    override fun generate(): FileSpec {
        logger.info("Generating $EXTENSION_NAME for Kotlin.")
        return FileSpec.builder(GENERATOR_PACKAGE, EXTENSION_NAME)
            .addType(
                TypeSpec.objectBuilder(EXTENSION_NAME)
                    .addFunction(getReadStringFunSpec())
                    .addFunction(getAttributeOrNullFunSpec())
                    .addFunction(getElementByTagFunSpec())
                    .addFunction(getBooleanConversionFunSpec())
                    .build()
            )
            .build()
    }

    private fun getReadStringFunSpec() = FunSpec.builder(METHOD_READ_STRING)
        .receiver(elementClass)
        .addParameter("name", String::class)
        .addParameter(
            ParameterSpec
                .builder("parentTag", String::class.asTypeName().copy(nullable = true))
                .defaultValue("null")
                .build()
        )
        .addCode(
            """
            |val nodeList = getElementsByTagName(name)
            |if (parentTag == null) {
            |${TAB}return nodeList.item(0)?.textContent
            |} else {
            |${TAB}for (i in 0 until nodeList.length) {
            |${TAB}${TAB}val e = nodeList.item(i) as? Element ?: continue
            |${TAB}${TAB}val parent = e.parentNode as? Element
            |${TAB}${TAB}if (parent?.tagName != parentTag) continue
            |${TAB}
            |${TAB}${TAB}return e.textContent
            |${TAB}}
            |${TAB}return null
            |}
            """.trimMargin()
        )
        .returns(String::class.asTypeName().copy(nullable = true))
        .build()

    private fun getAttributeOrNullFunSpec() = FunSpec.builder(METHOD_GET_ATTR_OR_NULL)
        .receiver(elementClass)
        .addParameter("tag", String::class)
        .addCode(
            """
            |val attr = getAttribute(tag) ?: return null
            |return if (attr.isEmpty() || attr.isBlank()) null else attr
            """.trimMargin()
        )
        .returns(String::class.asTypeName().copy(nullable = true))
        .build()

    private fun getElementByTagFunSpec() = FunSpec.builder(METHOD_GET_ELEMENT_BY_TAG)
        .receiver(elementClass)
        .addParameter("tag", String::class)
        .addCode(
            """
            |val nodeList = getElementsByTagName(tag)
            |if (nodeList.length == 0) return null
            |return nodeList.item(0) as? Element 
            """.trimMargin()
        )
        .returns(Element::class.asTypeName().copy(nullable = true))
        .build()
}