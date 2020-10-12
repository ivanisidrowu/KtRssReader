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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import tw.ktrssreader.processor.const.*

class ReaderGenerator(
    private val rootClassName: String,
    rootClassPackage: String
) : Generator {

    companion object {
        private const val URL = "url"
        private const val CONFIG = "config"
    }

    private val readerClassName = ClassName("tw.ktrssreader", "Reader")
    private val configClassName = ClassName("tw.ktrssreader.config", "KtRssReaderConfig")

    // KtRssReaderConfig.() -> Unit
    private val configTypeName = LambdaTypeName.get(
        receiver = configClassName,
        returnType = Unit::class.asClassName()
    )
    private val configParameter =
        ParameterSpec.builder(CONFIG, configTypeName)
            .defaultValue("{}")
    private val outputClassName = ClassName(rootClassPackage, rootClassName)
    private val parserName = "$rootClassName$PARSER_SUFFIX"
    private val parserClassName = ClassName(GENERATOR_PACKAGE, parserName)

    override fun generate(): FileSpec {
        val outputName = "$rootClassName$READER_NAME"
        return FileSpec.builder(GENERATOR_PACKAGE, outputName)
            .addType(
                TypeSpec
                    .objectBuilder(outputName)
                    .addFunction(getReadFunSpec())
                    .addFunction(getFlowReadFunSpec())
                    .addFunction(getCoReadFunSpec())
                    .build()
            )
            .build()
    }

    private fun getReadFunSpec(): FunSpec {
        return FunSpec.builder(METHOD_READ)
            .addParameter(URL, String::class)
            .addParameter(configParameter.build())
            .addModifiers(KModifier.INLINE)
            .addCode(
                "return %T.read<%T>(url = $URL, customParser = { xml -> %T.parse(xml) }, $CONFIG)",
                readerClassName,
                outputClassName,
                parserClassName
            )
            .returns(outputClassName)
            .build()
    }

    private fun getFlowReadFunSpec(): FunSpec {
        val flowClassName = ClassName("kotlinx.coroutines.flow", "Flow")
        return FunSpec.builder(METHOD_FLOW_READ)
            .addParameter(URL, String::class)
            .addParameter(
                configParameter
                    .addModifiers(KModifier.CROSSINLINE)
                    .build()
            )
            .addModifiers(KModifier.INLINE)
            .addStatement(
                "return %T.flowRead<%T>(url = $URL, customParser = { xml -> %T.parse(xml) }, $CONFIG)",
                readerClassName,
                outputClassName,
                parserClassName
            )
            .returns(flowClassName.parameterizedBy(outputClassName))
            .build()
    }

    private fun getCoReadFunSpec(): FunSpec {
        return FunSpec.builder(METHOD_CO_READ)
            .addParameter(URL, String::class)
            .addParameter(
                configParameter
                    .addModifiers(KModifier.CROSSINLINE)
                    .build()
            )
            .addModifiers(KModifier.INLINE, KModifier.SUSPEND)
            .addStatement(
                "return %T.coRead<%T>(url = $URL, customParser = { xml -> %T.parse(xml) }, $CONFIG)",
                readerClassName,
                outputClassName,
                parserClassName
            )
            .returns(outputClassName)
            .build()
    }
}