package tw.ktrssreader.persistence.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface DaoBase<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: T)

    @Delete
    fun delete(entity: T)
}