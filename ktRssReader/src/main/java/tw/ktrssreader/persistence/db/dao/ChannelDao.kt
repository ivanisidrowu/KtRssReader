package tw.ktrssreader.persistence.db.dao

import androidx.room.Dao
import androidx.room.Query
import tw.ktrssreader.persistence.db.entity.ChannelEntity

@Dao
interface ChannelDao : DaoBase<ChannelEntity> {

    @Query("SELECT * FROM CHANNEL_TABLE WHERE url = :url")
    fun getChannelByUrl(url: String): ChannelEntity
}