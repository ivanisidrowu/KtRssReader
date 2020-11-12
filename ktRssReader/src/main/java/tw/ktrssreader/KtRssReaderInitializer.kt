package tw.ktrssreader

import android.content.Context
import androidx.startup.Initializer

class KtRssReaderInitializer : Initializer<Unit> {

    companion object {
        lateinit var applicationContext: Context
    }

    override fun create(context: Context) {
        applicationContext = context
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}