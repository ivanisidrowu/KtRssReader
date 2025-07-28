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

import java.util.*
import kotlin.reflect.KClass
import tw.ktrssreader.processor.const.*

private val stringName = String::class.simpleName!!

private val primitiveNames = listOf(
    stringName,
    Int::class.simpleName!!,
    Boolean::class.simpleName!!,
    Long::class.simpleName!!,
    Short::class.simpleName!!
)

fun String.isListType(): Boolean {
    return contains(List::class.qualifiedName!!)
}

fun String.isTypeString(): Boolean = this.filterQuestionMark() == stringName

fun String.isPrimitive(): Boolean = primitiveNames.any { this.contains(other = it, ignoreCase = true) }

fun String.getFuncName(): String {
    return when {
        this.startsWith(GOOGLE_PREFIX) || this.startsWith(ITUNES_PREFIX) ->
            "get${this.substringAfterLast(':').capitalize()}"
        else -> "get${this.capitalize()}"
    }
}

fun String.getListFuncName(): String {
    val tagCapitalized = substringAfterLast(':').capitalize()
    return when {
        startsWith(GOOGLE_PREFIX) -> "get${GOOGLE_PREFIX.capitalize()}${tagCapitalized}List"
        startsWith(ITUNES_PREFIX) -> "get${ITUNES_PREFIX.capitalize()}${tagCapitalized}List"
        else -> "get${tagCapitalized}List"
    }
}

internal fun String.capitalize(): String = replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(Locale.ROOT)
    } else {
        it.toString()
    }
}

fun String.getGeneratedClassPath() =
    "$GENERATOR_PACKAGE.${this.capitalize().filterQuestionMark()}$PARSER_SUFFIX"

fun String.extractListType() =
    this.substringAfter('<')
        .substringBefore('>')
        .extractType()

fun String.extractType() =
    this.substringAfterLast('.')

fun String.getKPrimitiveClass(): KClass<*>? {
    return when {
        this.contains(String::class.simpleName!!, ignoreCase = true) -> String::class
        this.contains(Int::class.simpleName!!, ignoreCase = true) -> Int::class
        this.contains(Boolean::class.simpleName!!, ignoreCase = true) -> Boolean::class
        this.contains(Long::class.simpleName!!, ignoreCase = true) -> Long::class
        this.contains(Short::class.simpleName!!, ignoreCase = true) -> Short::class
        else -> null
    }
}

fun String.takeIfNotEmpty(): String? {
    return takeIf { it.isNotEmpty() }
}

fun String.getVariableName(tag: String): String {
    val tagCapitalized = tag.substringAfterLast(':').capitalize()
    return when {
        tag.startsWith(GOOGLE_PREFIX) -> "$this${GOOGLE_PREFIX.capitalize()}$tagCapitalized"
        tag.startsWith(ITUNES_PREFIX) -> "$this${ITUNES_PREFIX.capitalize()}$tagCapitalized"
        else -> "$this$tagCapitalized"
    }
}

fun String.appendTypeConversion(typeString: String): String {
    return when {
        typeString.contains(String::class.simpleName!!, ignoreCase = true) -> this
        typeString.contains(Int::class.simpleName!!, ignoreCase = true) -> "$this?.toIntOrNull()"
        typeString.contains(Boolean::class.simpleName!!, ignoreCase = true) -> "$this?.%M()"
        typeString.contains(Long::class.simpleName!!, ignoreCase = true) -> "$this?.toLongOrNull()"
        typeString.contains(Short::class.simpleName!!, ignoreCase = true) -> "$this?.toShortOrNull()"
        else -> this
    }
}

fun String.isBooleanType() = filterQuestionMark()
    .equals(Boolean::class.simpleName!!, ignoreCase = true)

fun String.filterQuestionMark() = replace("?", "")
