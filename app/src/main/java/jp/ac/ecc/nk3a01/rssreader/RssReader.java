package jp.ac.ecc.nk3a01.rssreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * 非同期の処理がAsyncTackクラスを使わなくてもokhttp3でできちゃいます
 */
public class RssReader extends AsyncTask<Object, Void, ArrayList<String>> {

    // private TextView textView;
    // 呼び出しもとのActivity
    private ListView listView;
    private Activity mainActivity;
    private ArrayList<String> arrayURL = new ArrayList<String>();
    //String result = "hogehoge";
    private static final int URL = 0;
    private static final int LISTVIEW = 1;
    private static final int MAXVIEWSIZE = 20;

    public RssReader(Activity activity) {
        this.mainActivity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(Object ... params) {
        ArrayList<String> result = new ArrayList<>();
        String url = (String)params[URL];
        this.listView = (ListView)params[LISTVIEW];

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 戻り値をArrayListにしちゃったのでとりあえずLogに出力にする
                // result = "not Successful";
            }
            if (response.body() != null) {
                // HTTPステータスコードを取得
                // result = String.valueOf(response.code());
                String body = response.body().string();
                InputStream inputStream = new ByteArrayInputStream(body.getBytes("UTF-8"));
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(inputStream));
                List<SyndEntry> list = feed.getEntries();
                // result = list.get(0).getTitle();
                // result = feed.getTitle();

                // 最初の記事が広告のため２個めから２０件取得
                // 最大２０件分タイトルを取得する
                for (int index = 1; index <= MAXVIEWSIZE; index++) {
                    SyndEntry entry = list.get(index);
                    result.add(entry.getTitle());
                    arrayURL.add(entry.getLink());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 目標：HTTPステータスコードを画面に表示するところまで
     * @param result
     */
    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
        // textView.setText(result);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, result);
        listView.setAdapter(adapter);
        this.setListViewOnClick(listView);
    }

    private void setListViewOnClick(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(arrayURL.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mainActivity.startActivity(intent);
            }
        });
    }

    /*
    非同期使う前の処理
    public String getTitle() {
        try {
            String url = "http://weather.livedoor.com/forecast/rss/earthquake.xml";
            try (Response response = client.newCall(request).execute()) {
                result = "hogehoge";
                if (!response.isSuccessful()) {
                    result =  "no successful";
                }

                if (response.body() != null) {
                    result = response.body().toString();
                } else {
                    result = "no body!!";
                }
            }

        } catch (MalformedURLException e) {
            result = "MalformedURLException";
            e.printStackTrace();
        } catch (IOException e) {
            result = "IOException";
            e.printStackTrace();
        }
        return result;
    }
    */
}
