package com.ead.project.dreamer.data.models;

import androidx.annotation.Nullable;

import com.ead.project.dreamer.app.data.util.HttpUtil;
import com.ead.project.dreamer.data.network.DreamerWebView;
import com.ead.project.dreamer.data.utils.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;


public class Server {

    private final List<VideoModel> videoList;
    public Player player;
    public Boolean isDirect;
    protected Boolean isDownloading;
    public String url;
    public final String DEFAULT = "Default";
    @Nullable
    protected DreamerWebView webView;

    @Inject
    public Server(String embeddedUrl) {
        this.url = embeddedUrl;
        videoList = new ArrayList<>();
        isDirect = true;
        isDownloading = getIsDownloading();
        onPreExtract();
        onExtract();
        OnExtractEnded();
    }

    protected int CONNECTION_STABLE = 6000;

    protected int CONNECTION_MIDDLE_STABLE = 80000;

    protected int CONNECTION_UNSTABLE =  16000;

    protected void onPreExtract() {}

    protected void onExtract() {}

    public void OnExtractEnded() {}

    public List<VideoModel> getVideoList() {return videoList;}

    protected void removeVideos() { videoList.clear(); }

    protected void addDefaultVideo() { this.videoList.add(new VideoModel(DEFAULT,url)); }

    protected void addVideo(VideoModel video) { this.videoList.add(video); }

    public Boolean isConnectionValidated() {
        if (videoList.isEmpty()) return false;
        return HttpUtil.INSTANCE
                .connectionAvailable(getVideoList().get(getVideoList().size()-1).getDirectLink());
    }

    protected Boolean isConnectionNotValidated() { return !isConnectionValidated(); }

    protected void runUI(Function0<Unit> unit) { Thread.INSTANCE.onUi(unit); }

    protected void handleDownload(int timeLimit) { if(awaitInTimePattern(timeLimit)) addDefaultVideo();}

    private Boolean awaitInTimePattern(int timeLimit) {
        awaitUntilInstance();
        return awaitUntilLoadingTime(timeLimit);
    }

    private void awaitUntilInstance() {
        int timeToInstance = 250;
        while (webView == null) {
            try {
                java.lang.Thread.sleep(timeToInstance);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean awaitUntilLoadingTime(int timeLimit) {
        int count = 0;
        int interval = 250;
        while (true) {
            assert webView != null;
            if (!webView.isLoading() || count > timeLimit) break;
            try {
                java.lang.Thread.sleep(interval); count += interval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return  count < timeLimit;
    }

    protected void complainWebView() {
        if (webView != null) {
            webView.setDownloadListener((urlSender, s1, s2, s3, l) -> {
                url = urlSender;
                webView.setLoading(false);
            });
            webView.loadUrl(url);
        }
    }

    protected void releaseWebView() {
        if (webView != null) {
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
    }

    private Boolean getIsDownloading() {
        return com.ead.project.dreamer.app.data.server.
                Server.INSTANCE.isDownloading();
    }

    public void endProcessing() {
        if (isAutomaticResolverActivated() && !videoList.isEmpty()) {
            setProcessed(true);
        }
    }

    public static Boolean isProcessEnded() {
        return isAutomaticResolverActivated() && isProcessed();
    }

    public static void endAutomaticResolver() {
        setProcessed(false);
    }

    private static Boolean isAutomaticResolverActivated() {
        return com.ead.project.dreamer.app.data.server.
                Server.INSTANCE.isAutomaticResolverActivated();
    }

    private static Boolean isProcessed() {
        return com.ead.project.dreamer.app.data.server.
                Server.INSTANCE.isProcessed();
    }

    private static void setProcessed(Boolean value) {
        com.ead.project.dreamer.app.data.server.Server.INSTANCE.setProcessed(value);
    }

    private List<Player> validatedServer () {
        return Arrays.asList(Player.Okru,Player.Onefichier,Player.Videobin,
                Player.SolidFiles,Player.Mp4Upload,Player.Uqload);
    }

    public Boolean isValidated () {return !videoList.isEmpty() && validatedServer().contains(this.player);}
}
