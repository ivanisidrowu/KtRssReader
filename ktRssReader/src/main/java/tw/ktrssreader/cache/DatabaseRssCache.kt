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

package tw.ktrssreader.cache

import tw.ktrssreader.KtRssReaderInitializer
import tw.ktrssreader.constant.Const
import tw.ktrssreader.persistence.db.entity.ChannelEntity
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.convertToByteArray
import tw.ktrssreader.utils.convertToObject
import tw.ktrssreader.utils.logD
import java.util.*

class DatabaseRssCache<T> : RssCache<T> {

    private val logTag = this::class.java.simpleName
    private val db = KtRssProvider.provideDatabase(KtRssReaderInitializer.applicationContext)
    private val dao = db.channelDao()

    override fun readCache(url: String, type: @Const.ChannelType Int, expiredTimeMillis: Long): T? {
        val entity = dao.getChannel(url, type) ?: return null

        val isCacheValid = (entity.time + expiredTimeMillis) > Calendar.getInstance().timeInMillis
        return if (isCacheValid) {
            entity.channel.convertToObject() as? T
        } else {
            dao.delete(entity)
            null
        }
    }

    override fun saveCache(url: String, channel: Any) {
        logD(logTag, "[saveCache] url: $url")

        dao.insert(
            ChannelEntity(
                url = url,
                type = Const.ChannelType.convertChannelToType(channel),
                channel = channel.convertToByteArray(),
                time = Calendar.getInstance().timeInMillis
            )
        )
    }

    override fun removeCache(url: String) {
        logD(logTag, "[removeCache] url: $url")

        dao.deleteByUrl(url)
    }
}