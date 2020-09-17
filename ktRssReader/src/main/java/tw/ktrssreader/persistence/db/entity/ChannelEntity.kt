package tw.ktrssreader.persistence.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_table")
class ChannelEntity(
    @PrimaryKey
    val url: String,
    val channel: ByteArray,
    val time: Long
)