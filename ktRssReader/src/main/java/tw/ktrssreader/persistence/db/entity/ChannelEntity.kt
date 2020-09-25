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

package tw.ktrssreader.persistence.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_table")
data class ChannelEntity(
    @PrimaryKey
    val url: String,
    val type: Int,
    val channel: ByteArray,
    val time: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelEntity

        if (url != other.url) return false
        if (!channel.contentEquals(other.channel)) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + channel.contentHashCode()
        result = 31 * result + time.hashCode()
        return result
    }
}
