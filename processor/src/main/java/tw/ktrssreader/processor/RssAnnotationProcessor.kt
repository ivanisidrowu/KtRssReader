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

import com.google.auto.service.AutoService
import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.processor.const.CHANNEL
import tw.ktrssreader.processor.util.Logger
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


@SupportedOptions("debug")
@AutoService(Processor::class)
class RssAnnotationProcessor : AbstractProcessor() {

    private var isExtensionGenerated = false

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            RssTag::class.java.canonicalName,
            RssRawData::class.java.canonicalName,
            RssAttribute::class.java.canonicalName
        )
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        val logger = Logger(processingEnv.messager)
        // This 'process' method could be called multiple times, so we use a flag to prevent it generate multiple times.
        if (!isExtensionGenerated) {
            ExtensionGenerator(logger).generate().writeTo(processingEnv.filer)
            isExtensionGenerated = true
        }

        p1?.getElementsAnnotatedWith(RssTag::class.java)?.forEach {
            if (it != null && it.kind == ElementKind.CLASS) {
                val rssTag = it.getAnnotation(RssTag::class.java)
                ParserGenerator(
                    element = it,
                    isRoot = rssTag?.name == CHANNEL,
                    logger = logger
                ).generate().writeTo(processingEnv.filer)
            }
        }
        return true
    }
}