package tw.ktrssreader.parser

import okio.ByteString.Companion.readByteString
import java.io.InputStream
import java.lang.StringBuilder
import java.nio.charset.Charset

object XmlFileReader {
    fun readFile(filename: String): String {
        val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream(filename)
        val streamBuilder = StringBuilder()

        var availableCount = inputStream.available()
        while (availableCount > 0) {
            val string = inputStream.readByteString(availableCount).string(Charset.forName("UTF-8"))
            streamBuilder.append(string)

            availableCount = inputStream.available()
        }

        return streamBuilder.toString()
    }
}