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

package tw.ktrssreader.kotlin.extension

import tw.ktrssreader.kotlin.model.channel.Image

fun Image?.replaceInvalidUrlByPriority(vararg priorityHref: String?): Image? {
    if (this == null || url != null) return this
    val href = priorityHref.firstOrNull { null != it } ?: return this

    return Image(
        link = link,
        title = title,
        url = href,
        description = description,
        height = height,
        width = width
    )
}

fun String?.hrefToImage(): Image? {
    this ?: return null

    return Image(
        link = null,
        title = null,
        url = this,
        description = null,
        height = null,
        width = null
    )
}