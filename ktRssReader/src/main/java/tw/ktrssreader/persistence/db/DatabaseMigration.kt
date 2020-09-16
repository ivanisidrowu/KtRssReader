package tw.ktrssreader.persistence.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class DatabaseMigration {

    fun getMigrations(): Array<Migration> = arrayOf()

    private val migrateFrom1To2 = migrate(1, 2) {
    }

    private inline fun migrate(
        from: Int,
        to: Int,
        crossinline action: SupportSQLiteDatabase.() -> Unit
    ): Migration {
        return object : Migration(from, to) {
            override fun migrate(database: SupportSQLiteDatabase) = action(database)
        }
    }
}