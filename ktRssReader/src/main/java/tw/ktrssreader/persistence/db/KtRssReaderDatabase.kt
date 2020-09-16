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
        private var INSTANCE: KtRssReaderDatabase? = null

        fun getInstance(applicationContext: Context): KtRssReaderDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    applicationContext,
                    KtRssReaderDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(*DatabaseMigration().getMigrations())
                    .build()
                    .apply { INSTANCE = this }
            }
        }
    }

    abstract fun channelDao(): ChannelDao
}