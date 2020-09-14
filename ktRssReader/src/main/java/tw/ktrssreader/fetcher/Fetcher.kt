package tw.ktrssreader.fetcher

import kotlinx.coroutines.flow.Flow

interface Fetcher {
    fun fetch(url: String): Flow<String>
}