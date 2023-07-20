package tw.ktrssreader

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.ktrssreader.reader.RssReader
import tw.ktrssreader.reader.RssType
import java.nio.charset.Charset
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DEFAULT_RSS_URL = "https://feeds.fireside.fm/wzd/rss"
        private const val DEFAULT_CHARSET = "UTF-8"
        private val RSS_URL_LIST = listOf(
            "https://www.mirrormedia.mg/rss/category_readforyou.xml?utm_source=feed_related&utm_medium=itunes",
            "https://feeds.fireside.fm/lushu/rss",
            "https://sw7x7.libsyn.com/rss",
            "https://rss.art19.com/real-estate-rookie",
            "https://rss.art19.com/so-you-want-to-work-abroad",
            "https://feeds.megaphone.fm/mad-money",
            "https://www.rnz.co.nz/acast/flying-solo.rss",
            "https://rss.whooshkaa.com/rss/podcast/id/6175",
            "https://rss.acast.com/how-i-work",
            "https://journeytolaunch.libsyn.com/rss",
            "https://anchor.fm/s/12680fe4/podcast/rss",
            "https://blazingtrails.libsyn.com/rss",
            "https://anchor.fm/s/12746230/podcast/rss",
            "https://rss.art19.com/wecrashed",
            "https://consciouscreators.libsyn.com/rss",
        )
    }

    private val rssType
        get() = spinner.selectedItem as? RssType ?: error("Invalid item was clicked!")

    private val rssText
        get() = etRss.text.toString()

    private val useCache
        get() = rbCacheYes.isChecked

    private val charsets = hashMapOf<String, Charset>()
    private val charset: Charset
        get() {
            val charsetText = etCharset.text.toString()
            return charsets.getOrPut(charsetText) {
                Charset.forName(charsetText)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, RssType.values())

        etRss.setText(DEFAULT_RSS_URL)
        etCharset.setText(DEFAULT_CHARSET)

        btnRead.setOnClickListener { read() }
        btnCoroutine.setOnClickListener { coRead() }
        btnFlow.setOnClickListener { flowRead() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        RSS_URL_LIST.forEach { url -> menu.add(Menu.NONE, View.generateViewId(), Menu.NONE, url) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        etRss.setText(item.title)
        return true
    }

    private fun read() {
        progressBar.visibility = View.VISIBLE
        textView.text = null

        thread {
            try {
                val channel = RssReader.read(rssType, rssText, useCache, charset)

                runOnUiThread {
                    textView.text = channel.toString()
                    progressBar.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    textView.text = e.toString()
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun coRead() {
        lifecycleScope.launch {
            textView.text = null
            progressBar.visibility = View.VISIBLE

            try {
                val channel = withContext(Dispatchers.IO) {
                    RssReader.coRead(rssType, rssText, useCache, charset)
                }

                textView.text = channel.toString()
                progressBar.visibility = View.INVISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                textView.text = e.toString()
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun flowRead() {
        lifecycleScope.launch {
            val flowChannel = RssReader.flowRead(rssType, rssText, useCache, charset)

            flowChannel.flowOn(Dispatchers.IO)
                .onStart {
                    textView.text = null
                    progressBar.visibility = View.VISIBLE
                }.onEach {
                    progressBar.visibility = View.INVISIBLE
                }.catch { e ->
                    e.printStackTrace()
                    textView.text = e.toString()
                    progressBar.visibility = View.INVISIBLE
                }.collect { channel ->
                    textView.text = channel.toString()
                }
        }
    }
}
