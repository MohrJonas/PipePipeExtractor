package org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip;

import javax.annotation.Nonnull;

public final class TwitchClipResponseInner {

    @Nonnull
    private final String clipThumbnailUrl;

    @Nonnull
    private final String gameName;

    @Nonnull
    private final String clipTitle;

    @Nonnull
    private final String clipId;

    @Nonnull
    private final String uploadDateTimeString;

    @Nonnull
    private final String clipperName;

    private final int clipViewerCount;

    private final int clipLength;

    public TwitchClipResponseInner(@Nonnull String clipThumbnailUrl, @Nonnull String gameName, @Nonnull String clipTitle, @Nonnull String clipId, @Nonnull String uploadDateTimeString, @Nonnull String clipperName, int clipViewerCount, int vodLength) {
        this.clipThumbnailUrl = clipThumbnailUrl;
        this.gameName = gameName;
        this.clipTitle = clipTitle;
        this.clipId = clipId;
        this.uploadDateTimeString = uploadDateTimeString;
        this.clipperName = clipperName;
        this.clipViewerCount = clipViewerCount;
        this.clipLength = vodLength;
    }

    @Nonnull
    public String getClipThumbnailUrl() {
        return clipThumbnailUrl;
    }

    @Nonnull
    public String getGameName() {
        return gameName;
    }

    @Nonnull
    public String getClipTitle() {
        return clipTitle;
    }

    @Nonnull
    public String getUploadDateTimeString() {
        return uploadDateTimeString;
    }

    public int getClipViewerCount() {
        return clipViewerCount;
    }

    public int getClipLength() {
        return clipLength;
    }

    @Nonnull
    public String getClipperName() {
        return clipperName;
    }

    @Nonnull
    public String getClipId() {
        return clipId;
    }
}
