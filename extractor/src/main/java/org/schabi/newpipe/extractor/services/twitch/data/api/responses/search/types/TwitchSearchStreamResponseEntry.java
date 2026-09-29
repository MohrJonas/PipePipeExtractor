package org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.types;

import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.TwitchSearchBaseResponseEntry;

import javax.annotation.Nonnull;

public final class TwitchSearchStreamResponseEntry extends TwitchSearchBaseResponseEntry {

    @Nonnull
    private final String channelName;

    @Nonnull
    private final String streamTitle;

    private final int streamViewerCount;

    @Nonnull
    private final String streamThumbnailUrl;

    @Nonnull
    private final String streamGameName;

    @Nonnull
    private final String channelAvatarUrl;

    public TwitchSearchStreamResponseEntry(@Nonnull String channelName, @Nonnull String streamTitle, int streamViewerCount, @Nonnull String streamThumbnailUrl, @Nonnull String streamGameName, @Nonnull String channelAvatarUrl) {
        this.channelName = channelName;
        this.streamTitle = streamTitle;
        this.streamViewerCount = streamViewerCount;
        this.streamThumbnailUrl = streamThumbnailUrl;
        this.streamGameName = streamGameName;
        this.channelAvatarUrl = channelAvatarUrl;
    }

    @Nonnull
    public String getChannelName() {
        return channelName;
    }

    @Nonnull
    public String getStreamTitle() {
        return streamTitle;
    }

    public int getStreamViewerCount() {
        return streamViewerCount;
    }

    @Nonnull
    public String getStreamThumbnailUrl() {
        return streamThumbnailUrl;
    }

    @Nonnull
    public String getStreamGameName() {
        return streamGameName;
    }

    @Nonnull
    public String getChannelAvatarUrl() {
        return channelAvatarUrl;
    }
}
