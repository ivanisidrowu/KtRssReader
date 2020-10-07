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

package tw.ktrssreader.processor.util

import tw.ktrssreader.processor.const.GENERATOR_PACKAGE
import tw.ktrssreader.processor.const.PARSER_SUFFIX
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import kotlin.reflect.KClass

private val primitiveJavaPaths = listOf(
    String::class.java.canonicalName,
    Int::class.java.canonicalName,
    Boolean::class.java.canonicalName,
    Long::class.java.canonicalName,
    Short::class.java.canonicalName
)

fun String.isListType(): Boolean {
    return contains(List::class.java.canonicalName)
}

fun String.isPrimitive(): Boolean = primitiveJavaPaths.any { this.contains(it) }

fun String.getFuncName() = "get${this.capitalize(Locale.ROOT)}"

fun String.getGeneratedClassPath() =
    "${GENERATOR_PACKAGE}.${this.capitalize(Locale.ROOT)}${PARSER_SUFFIX}"

fun String.extractListType() =
    this.substringAfter('<')
        .substringBefore('>')
        .extractType()

fun String.extractType() =
    this.substringAfterLast('.')

fun String.getKPrimitiveClass(): KClass<*>? {
    return when (this) {
        String::class.java.simpleName -> String::class
        Integer::class.java.simpleName -> Int::class
        Boolean::class.java.simpleName -> Boolean::class
        Long::class.java.simpleName -> Long::class
        Short::class.java.simpleName -> Short::class
        else -> null
    }
}

fun String.takeIfNotEmpty(): String? {
    return takeIf { it.isNotEmpty() }
}

fun Element.getPackage(): String {
    var thisElement = this
    while (thisElement.kind != ElementKind.PACKAGE) {
        thisElement = thisElement.enclosingElement
    }

    return (thisElement as PackageElement).qualifiedName.toString()
}