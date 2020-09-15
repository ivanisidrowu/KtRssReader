package tw.ktrssreader.fetcher

interface Fetcher {
    fun fetch(url: String): String
}