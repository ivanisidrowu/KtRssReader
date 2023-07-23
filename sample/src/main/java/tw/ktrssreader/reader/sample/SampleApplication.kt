package tw.ktrssreader.reader.sample

import android.app.Application
import tw.ktrssreader.config.readerGlobalConfig

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        readerGlobalConfig {
            enableLog = true
        }
    }
}
