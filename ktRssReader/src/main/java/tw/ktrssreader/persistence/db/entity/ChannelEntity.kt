package tw.ktrssreader.persistence.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_table")
class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: Int,
    val channel: ByteArray,
    val time: Long
)