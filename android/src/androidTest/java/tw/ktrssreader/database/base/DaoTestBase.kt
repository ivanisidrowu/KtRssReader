package tw.ktrssreader.database.base

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import tw.ktrssreader.persistence.db.KtRssReaderDatabase

open class DaoTestBase {

    protected lateinit var database: KtRssReaderDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, KtRssReaderDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }
}
