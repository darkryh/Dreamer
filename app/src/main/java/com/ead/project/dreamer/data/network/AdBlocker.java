package com.ead.project.dreamer.data.network;

import android.content.Context;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.BufferedSource;
import okio.Okio;

public class AdBlocker {
    private static final String HOSTS_FILE = "host.txt";
    private static final Set<String> HOSTS = new HashSet<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void init(Context context) {
        executor.execute(() -> { //Background work here
            try {
                loadFromAssets(context);
            } catch (IOException e) {
                /*noop*/
            }
        });
    }

    @WorkerThread
    private static void loadFromAssets(Context context) throws IOException {
        InputStream stream = context.getAssets().open(HOSTS_FILE);
        BufferedSource buffer = Okio.buffer(Okio.source(stream));
        String line;
        while ((line = buffer.readUtf8Line()) != null) {
            HOSTS.add(line);
        }
        buffer.close();
        stream.close();
    }

    public static boolean isPermitted(String url) {
        try {
            return isPermittedHost(getHost(url));
        } catch (MalformedURLException e) {
            Log.e("error host..", e.toString());
            return false;
        }
    }

    private static boolean isPermittedHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        int index = host.indexOf(".");
        return index >= 0 && (HOSTS.contains(host) ||
                index + 1 < host.length() && isPermittedHost(host.substring(index + 1)));
    }

    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }

    public static String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }

}
