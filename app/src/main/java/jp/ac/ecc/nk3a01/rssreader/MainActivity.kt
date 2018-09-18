package jp.ac.ecc.nk3a01.rssreader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feedUrl = "http://weather.livedoor.com/forecast/rss/earthquake.xml"

        val rssReader: RssReader = RssReader(this)
        // 第２引数にListViewインスタンスを渡して非同期で画面を更新する
        rssReader.execute(feedUrl, listTitle)
    }
}
