package com.muchev.risto.hnrr.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import com.muchev.risto.hnrr.R;
import com.muchev.risto.hnrr.model.News;
import com.muchev.risto.hnrr.utils.DownloadHelper;
import com.muchev.risto.hnrr.utils.ParseRSSHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MSG_REFRESH = 1;
    private static final int REFRESH_TIME_MS = 60000;

    private static final int MSG_ITEM = 2;
    private static final int ITEM_TIME_MS = 1000;

    //ui
    private ListView lvNews = null;
    private ArrayAdapter<News> arrayAdapter = null;

    //task
    private NewsDownload task = null;

    //data
    private LinkedList<News> dataSource = null;
    private LinkedBlockingDeque<News> download = null;

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_REFRESH) {

                task = new NewsDownload();
                task.execute("https://news.ycombinator.com/rss");

                handler.sendEmptyMessageDelayed(MSG_REFRESH, REFRESH_TIME_MS);

            }else if(msg.what == MSG_ITEM){
                News news = null;
                if ((news = download.pollLast()) != null) {
                    if (!dataSource.contains(news)) {
                        dataSource.addFirst(news);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessageDelayed(MSG_ITEM, ITEM_TIME_MS);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvNews = (ListView)findViewById(R.id.lvNews);

        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News)parent.getItemAtPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(news.getLink()));
                startActivity(intent);
            }
        });

        dataSource = new LinkedList<News>();
        arrayAdapter = new ArrayAdapter<News>(
                MainActivity.this,
                R.layout.news_item,
                dataSource
        );

        lvNews.setAdapter(arrayAdapter);

        handler.sendEmptyMessage(MSG_REFRESH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler!=null){
            handler.removeMessages(MSG_ITEM);
            handler.removeMessages(MSG_REFRESH);

        }

        if(task!=null){
            task.cancel(true);
            task = null;
        }


    }

    private class NewsDownload extends AsyncTask<String, String, LinkedList<News>>{

        @Override
        protected LinkedList<News> doInBackground(String... params) {
            publishProgress(getString(R.string.started_download));
            String mFileContents = DownloadHelper.downloadRss(params[0]);
            if(mFileContents != null) {
                publishProgress(getString(R.string.download_complete_and_started_parsing));
                ParseRSSHelper parse = new ParseRSSHelper(mFileContents);
                if(parse.process()) {
                    publishProgress(getString(R.string.parsing_complete));
                    return parse.getNews();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(LinkedList<News> aVoid) {
            super.onPostExecute(aVoid);

            if(aVoid!=null) {

                if(download == null){
                    download = new LinkedBlockingDeque<>();
                }

                download.addAll(aVoid);

                handler.sendEmptyMessage(MSG_ITEM);
            }else{
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }

    }
}


