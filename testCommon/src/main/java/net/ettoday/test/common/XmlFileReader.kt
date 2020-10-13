package net.ettoday.test.common

import okio.ByteString.Companion.readByteString

object XmlFileReader {
    fun readFile(filename: String): String {
        val stringBuilder = StringBuilder()
        this::class.java.classLoader?.getResourceAsStream(filename)?.use { inputStream ->
            var availableCount = inputStream.available()
            while (availableCount > 0) {
                val string = inputStream.readByteString(availableCount).string(Charsets.UTF_8)
                stringBuilder.append(string)

                availableCount = inputStream.available()
            }
        }

        return stringBuilder.toString()
    }
}