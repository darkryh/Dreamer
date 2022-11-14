package com.ead.project.dreamer.data.models;

import androidx.annotation.Nullable;
import com.ead.project.dreamer.data.commons.Constants;
import com.ead.project.dreamer.data.commons.Tools;
import com.ead.project.dreamer.data.network.DreamerWebView;
import com.ead.project.dreamer.data.utils.DataStore;
import com.ead.project.dreamer.data.utils.ThreadUtil;
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
    protected final Boolean isDownloading;
    public String url;
    @Nullable
    protected String reference;
    public final String DEFAULT = "Default";
    @Nullable
    protected DreamerWebView webView;

    @Inject
    public Server(String embeddedUrl) {
        this.url = embeddedUrl;
        videoList = new ArrayList<>();
        isDirect = true;
        isDownloading = Constants.Companion.getDownloadMode();
        onPreExtract();
        onExtract();
        OnExtractEnded();
    }

    protected int CONNECTION_STABLE = 4000;

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
        try { return Tools.Companion
                    .isConnectionAvailable(this.getVideoList().get(getVideoList().size()-1).getDirectLink());
        } catch (Exception e) { return false; }
    }

    protected Boolean connectionAvailable() {
        if (!videoList.isEmpty()) return Tools.Companion.isConnectionAvailable(videoList.get(videoList.size() - 1).getDirectLink());
        return false;
    }

    protected void runUI(Function0<Unit> unit) {ThreadUtil.INSTANCE.onUi(unit);}

    protected void handleDownload(int timeLimit) { if(awaitInTimePattern(timeLimit)) addDefaultVideo();}

    private Boolean awaitInTimePattern(int timeLimit) {
        awaitUntilInstance();
        return awaitUntilLoadingTime(timeLimit);
    }

    private void awaitUntilInstance() {
        while (webView == null) {
            try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    private Boolean awaitUntilLoadingTime(int timeLimit) {
        int count = 0;
        while (true) {
            assert webView != null;
            if (!webView.isLoading() || count > timeLimit) break;
            try {Thread.sleep(250); count += 250;} catch (InterruptedException e) {e.printStackTrace();}}

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
            webView.destroy();
            webView = null;
        }
    }

    protected Boolean connectionIsNotAvailable() { return !connectionAvailable(); }

    public void breakOperation() {
        if (DataStore.Companion.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER,false) && !videoList.isEmpty())
            DataStore.Companion.writeBoolean(Constants.BREAK_SERVER_OPERATION,true);
    }

    public static Boolean isOperationBreak() {
        return DataStore.Companion.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER,false) &&
                DataStore.Companion.readBoolean(Constants.BREAK_SERVER_OPERATION,false);
    }

    public static void endOperation() { DataStore.Companion.writeBooleanAsync(Constants.BREAK_SERVER_OPERATION,false); }

    private List<Player> validatedServer () {
        return Arrays.asList(Player.Okru,Player.Onefichier,Player.Videobin,
                Player.SolidFiles,Player.Mp4Upload,Player.Uqload);
    }

    public Boolean isValidated () {return !this.videoList.isEmpty() && validatedServer().contains(this.player);}
}
