package tw.ktrssreader.processor

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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.writeTo
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.processor.const.CHANNEL
import tw.ktrssreader.processor.const.KSP_OPTION_KEY
import tw.ktrssreader.processor.generator.AndroidExtensionGenerator
import tw.ktrssreader.processor.generator.AndroidParserGenerator
import tw.ktrssreader.processor.generator.AndroidReaderParserGenerator
import tw.ktrssreader.processor.generator.KotlinExtensionGenerator
import tw.ktrssreader.processor.generator.KotlinParserGenerator

class KspProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private var isExtensionGenerated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val isPureKotlinParser = options.any { it.key == KSP_OPTION_KEY && it.value.toBoolean() }
        if (isPureKotlinParser) {
            generateClassesForKotlin(resolver)
        } else {
            generateClassesForAndroid(resolver)
        }
        return emptyList()
    }

    private fun generateClassesForKotlin(resolver: Resolver) {
        // This 'process' method could be called multiple times, so we use a flag to prevent it generate multiple times.
        if (!isExtensionGenerated) {
            KotlinExtensionGenerator(logger).generate().writeTo(codeGenerator, false)
            isExtensionGenerated = true
        }

        generateParsers(resolver) { isRoot, classDeclaration ->
            logger.info("[KspProcessor][generateClassesForKotlin] isRoot = $isRoot, classDeclaration = $classDeclaration")
            KotlinParserGenerator(
                declaration = classDeclaration,
                isRoot = isRoot,
                logger = logger
            ).generate().writeTo(codeGenerator, false, listOf(classDeclaration.containingFile!!))
        }
    }

    private fun generateClassesForAndroid(resolver: Resolver) {
        // This 'process' method could be called multiple times, so we use a flag to prevent it generate multiple times.
        if (!isExtensionGenerated) {
            AndroidExtensionGenerator(logger).generate().writeTo(codeGenerator, false)
            isExtensionGenerated = true
        }

        generateParsers(resolver) { isRoot, classDeclaration ->
            logger.info("[KspProcessor][generateClassesForAndroid] isRoot = $isRoot, classDeclaration = $classDeclaration")
            AndroidParserGenerator(
                classDeclaration = classDeclaration,
                isRoot = isRoot,
                logger = logger
            ).generate().writeTo(codeGenerator, false, listOf(classDeclaration.containingFile!!))
            if (isRoot) {
                AndroidReaderParserGenerator(
                    rootClassName = classDeclaration.simpleName.asString(),
                    rootClassPackage = classDeclaration.packageName.asString(),
                    logger = logger
                ).generate()
                    .writeTo(codeGenerator, false, listOf(classDeclaration.containingFile!!))
            }
        }
    }

    @OptIn(KspExperimental::class)
    private inline fun generateParsers(
        resolver: Resolver,
        crossinline action: (Boolean, KSClassDeclaration) -> Unit
    ) {
        val rssTagFullName = RssTag::class.qualifiedName ?: return
        resolver.getSymbolsWithAnnotation(rssTagFullName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { ksClassDeclaration ->
                val annotation: RssTag =
                    ksClassDeclaration.getAnnotationsByType(RssTag::class).firstOrNull()
                        ?: return@forEach
                logger.info("[KspProcessor][generateParsers]: class = ${ksClassDeclaration.simpleName.asString()}, annotation = ${annotation.name}")
                val isRoot = annotation.name == CHANNEL
                action(isRoot, ksClassDeclaration)
            }
    }
}
