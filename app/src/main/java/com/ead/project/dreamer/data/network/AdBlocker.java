package com.ead.project.dreamer.data.network;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;
import androidx.annotation.WorkerThread;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import okio.BufferedSource;
import okio.Okio;

public class AdBlocker {
    private static final String AD_HOSTS_FILE = "host.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();

    public static void init(Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loadFromAssets(context);
                } catch (IOException e) {
                    // noop
                }
                return null;
            }
        }.execute();
    }

    @WorkerThread
    private static void loadFromAssets(Context context) throws IOException {
        InputStream stream = context.getAssets().open(AD_HOSTS_FILE);
        BufferedSource buffer = Okio.buffer(Okio.source(stream));
        String line;
        while ((line = buffer.readUtf8Line()) != null) {
            AD_HOSTS.add(line);
        }
        buffer.close();
        stream.close();
    }

    public static boolean isAd(String url) {
        try {
            return isAdHost(getHost(url));
        } catch (MalformedURLException e) {
            Log.e("error host..", e.toString());
            return false;
        }
    }

    private static boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }

    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }

    public static String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }

}
