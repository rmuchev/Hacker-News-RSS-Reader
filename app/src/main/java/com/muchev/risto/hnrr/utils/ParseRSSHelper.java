package com.muchev.risto.hnrr.utils;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.LinkedList;

import com.muchev.risto.hnrr.model.News;

/**
 * Created by xuefeng on 4/1/2016.
 */
public class ParseRSSHelper {

    private static final String TAG = ParseRSSHelper.class.getSimpleName();

    private String data;
    private LinkedList<News> news;

    public ParseRSSHelper(String data) {
        this.data = data;
        news = new LinkedList<>();
    }

    public LinkedList<News> getNews() {
        return news;
    }

    public boolean process() {
        News item = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(this.data));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            inEntry = true;
                            item = new News();

                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (inEntry) {
                            if (tagName.equalsIgnoreCase("item")) {
                                news.add(item);
                                inEntry = false;
                            }
                            else if (tagName.equalsIgnoreCase("title")) {
                                item.setTitle(textValue);
                            }
                            else if (tagName.equalsIgnoreCase("link")) {
                                item.setLink(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType = xpp.next();
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }
}