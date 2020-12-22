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

package tw.ktrssreader.persistence.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tw.ktrssreader.persistence.db.dao.ChannelDao
import tw.ktrssreader.persistence.db.entity.ChannelEntity

@Database(entities = [ChannelEntity::class], version = KtRssReaderDatabase.DB_VERSION)
abstract class KtRssReaderDatabase : RoomDatabase() {

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "ktrssreader.db"

        @Volatile
        private var instance: KtRssReaderDatabase? = null

        fun getInstance(applicationContext: Context): KtRssReaderDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    applicationContext,
                    KtRssReaderDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(*DatabaseMigration().getMigrations())
                    .build()
                    .apply { instance = this }
            }
        }
    }

    abstract fun channelDao(): ChannelDao
}