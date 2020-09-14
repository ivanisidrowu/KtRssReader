package tw.ktrssreader.fetcher

import kotlinx.coroutines.flow.Flow

internal interface Fetcher {
    fun fetch(url: String): Flow<String>
}