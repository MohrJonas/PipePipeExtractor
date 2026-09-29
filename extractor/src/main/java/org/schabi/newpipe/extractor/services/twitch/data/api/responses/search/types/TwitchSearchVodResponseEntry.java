package org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.types;

import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.TwitchSearchBaseResponseEntry;

import javax.annotation.Nonnull;

public final class TwitchSearchVodResponseEntry extends TwitchSearchBaseResponseEntry {

    @Nonnull
    private final String vodTitle;

    @Nonnull
    private final String channelName;

    private final int vodViewCount;

    private final int vodDurationInSeconds;

    @Nonnull
    private final String streamThumbnailUrl;

    @Nonnull
    private final String uploadTimeStampString;

    @Nonnull
    private final String vodId;

    public TwitchSearchVodResponseEntry(@Nonnull String vodTitle, @Nonnull String channelName, int vodViewCount, int vodDurationInSeconds, @Nonnull String streamThumbnailUrl, @Nonnull String uploadTimeStampString, @Nonnull String vodId) {
        this.vodTitle = vodTitle;
        this.channelName = channelName;
        this.vodViewCount = vodViewCount;
        this.vodDurationInSeconds = vodDurationInSeconds;
        this.streamThumbnailUrl = streamThumbnailUrl;
        this.uploadTimeStampString = uploadTimeStampString;
        this.vodId = vodId;
    }

    @Nonnull
    public String getVodTitle() {
        return vodTitle;
    }

    @Nonnull
    public String getChannelName() {
        return channelName;
    }

    public int getVodViewCount() {
        return vodViewCount;
    }

    public int getVodDurationInSeconds() {
        return vodDurationInSeconds;
    }

    @Nonnull
    public String getStreamThumbnailUrl() {
        return streamThumbnailUrl;
    }

    @Nonnull
    public String getUploadTimeStampString() {
        return uploadTimeStampString;
    }

    @Nonnull
    public String getVodId() {
        return vodId;
    }
}
