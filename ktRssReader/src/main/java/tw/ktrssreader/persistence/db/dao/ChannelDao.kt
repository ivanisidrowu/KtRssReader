package tw.ktrssreader.persistence.db.dao

import androidx.room.Dao
import androidx.room.Query
import tw.ktrssreader.persistence.db.entity.ChannelEntity

@Dao
interface ChannelDao : DaoBase<ChannelEntity> {
    @Query("SELECT * FROM CHANNEL_TABLE WHERE type = :type ORDER BY ID DESC LIMIT 1")
    fun getLatestChannelByType(type: Int): ChannelEntity
}