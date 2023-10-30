package com.ead.project.dreamer.data.models;

import javax.inject.Inject;

public class EmbedServer extends Server {

    @Inject
    public EmbedServer(String embeddedUrl,Player player) {
        super(embeddedUrl,player);

        if (isDownloading) return;

        this.player = player;
        this.isDirect = false;

        this.url = setupEmbeddedUrl(embeddedUrl);

        if (checkIfVideoIsAvailable()) {
            addDefaultVideo();
        }
    }

    protected String setupEmbeddedUrl(String embeddedUrl) {
        return embeddedUrl;
    }

    protected Boolean checkIfVideoIsAvailable() {
        return false;
    }
}
