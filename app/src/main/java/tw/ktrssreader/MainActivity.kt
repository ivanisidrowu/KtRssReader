package tw.ktrssreader

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.ktrssreader.model.channel.AutoMixChannelData
import tw.ktrssreader.model.channel.GoogleChannelData
import tw.ktrssreader.model.channel.ITunesChannelData
import tw.ktrssreader.model.channel.RssStandardChannelData
import java.nio.charset.Charset
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DEFAULT_RSS_URL = "https://feeds.fireside.fm/wzd/rss"
        private const val DEFAULT_CHARSET = "UTF-8"
        private val RSS_URL_LIST = listOf(
            "https://feeds.fireside.fm/wzd/rss",
            "https://feeds.soundcloud.com/users/soundcloud:users:221361980/sounds.rss",
            "https://feeds.soundcloud.com/users/soundcloud:users:322164009/sounds.rss",
            "https://www.mirrormedia.mg/rss/category_readforyou.xml?utm_source=feed_related&utm_medium=itunes",
            "https://feeds.fireside.fm/lushu/rss",
            "https://sw7x7.libsyn.com/rss",
            "https://feeds.fireside.fm/starrocket/rss",
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                val channel = when {
                    rbRssStandard.isChecked -> {
                        Reader.read<RssStandardChannelData>(etRss.text.toString()) {
                            useRemote = rbCacheNo.isChecked
                            charset = Charset.forName(etCharset.text.toString())
                        }
                    }
                    rbRssItunes.isChecked -> {
                        Reader.read<ITunesChannelData>(etRss.text.toString()) {
                            useRemote = rbCacheNo.isChecked
                            charset = Charset.forName(etCharset.text.toString())
                        }
                    }
                    rbRssGooglePlay.isChecked -> {
                        Reader.read<GoogleChannelData>(etRss.text.toString()) {
                            useRemote = rbCacheNo.isChecked
                            charset = Charset.forName(etCharset.text.toString())
                        }
                    }
                    rbRssAutoMix.isChecked -> {
                        Reader.read<AutoMixChannelData>(etRss.text.toString()) {
                            useRemote = rbCacheNo.isChecked
                            charset = Charset.forName(etCharset.text.toString())
                        }
                    }
                    else -> throw Exception()
                }

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
                    when {
                        rbRssStandard.isChecked -> {
                            Reader.coRead<RssStandardChannelData>(etRss.text.toString()) {
                                useRemote = rbCacheNo.isChecked
                                charset = Charset.forName(etCharset.text.toString())
                            }
                        }
                        rbRssItunes.isChecked -> {
                            Reader.coRead<ITunesChannelData>(etRss.text.toString()) {
                                useRemote = rbCacheNo.isChecked
                                charset = Charset.forName(etCharset.text.toString())
                            }
                        }
                        rbRssGooglePlay.isChecked -> {
                            Reader.coRead<GoogleChannelData>(etRss.text.toString()) {
                                useRemote = rbCacheNo.isChecked
                                charset = Charset.forName(etCharset.text.toString())
                            }
                        }
                        rbRssAutoMix.isChecked -> {
                            Reader.coRead<AutoMixChannelData>(etRss.text.toString()) {
                                useRemote = rbCacheNo.isChecked
                                charset = Charset.forName(etCharset.text.toString())
                            }
                        }
                        else -> throw Exception()
                    }
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
            val flowChannel = when {
                rbRssStandard.isChecked -> {
                    Reader.flowRead<RssStandardChannelData>(etRss.text.toString()) {
                        useRemote = rbCacheNo.isChecked
                        charset = Charset.forName(etCharset.text.toString())
                    }
                }
                rbRssItunes.isChecked -> {
                    Reader.flowRead<ITunesChannelData>(etRss.text.toString()) {
                        useRemote = rbCacheNo.isChecked
                        charset = Charset.forName(etCharset.text.toString())
                    }
                }
                rbRssGooglePlay.isChecked -> {
                    Reader.flowRead<GoogleChannelData>(etRss.text.toString()) {
                        useRemote = rbCacheNo.isChecked
                        charset = Charset.forName(etCharset.text.toString())
                    }
                }
                rbRssAutoMix.isChecked -> {
                    Reader.flowRead<AutoMixChannelData>(etRss.text.toString()) {
                        useRemote = rbCacheNo.isChecked
                        charset = Charset.forName(etCharset.text.toString())
                    }
                }
                else -> return@launch
            }

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