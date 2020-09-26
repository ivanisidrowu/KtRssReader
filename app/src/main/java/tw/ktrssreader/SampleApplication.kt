package tw.ktrssreader

import android.app.Application
import tw.ktrssreader.config.readerGlobalConfig

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        readerGlobalConfig {
            setApplicationContext(this@SampleApplication)
            enableLog = true
        }
    }
}