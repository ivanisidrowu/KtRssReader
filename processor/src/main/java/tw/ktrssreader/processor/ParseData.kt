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

import javax.lang.model.element.Element

data class ParseData(
    val tag: String,
    val type: String?,
    val rawType: String?,
    val dataType: DataType,
    val listItemType: String?,
    val packageName: String?,
    val processorElement: Element,
)

enum class DataType {
    PRIMITIVE, LIST, ATTRIBUTE, OTHER
}