package tw.ktrssreader.persistence.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_table")
data class ChannelEntity(
    @PrimaryKey
    val url: String,
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
