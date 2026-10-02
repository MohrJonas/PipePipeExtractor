package org.schabi.newpipe.extractor.services.twitch;

import org.schabi.newpipe.extractor.services.twitch.data.TwitchChannelTabLinkType;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TwitchUrlBuilder {
    public static @Nonnull String buildSearchUrlFromSearchQuery(@Nonnull final String query) {
        try {
            return new URL(TwitchService.BaseUrl + "/search?term=" + query).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildChannelUrlFromChannelName(@Nonnull final String name) {
        try {
            return new URL(TwitchService.BaseUrl + "/" + name + "?_type=channel").toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildStreamUrlFromChannelName(@Nonnull final String name) {
        try {
            return new URL(TwitchService.BaseUrl + "/" + name + "?_type=stream").toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildCategoryUrlFromCategoryName(@Nonnull final String name, @Nonnull final String id, @Nullable final String cursor) {
        try {
            var urlString = TwitchService.BaseUrl + "/directory/category/" + name;
            urlString += "?_id=" + id;
            if(cursor != null)
                urlString += "&_cursor=" + URLEncoder.encode(cursor, StandardCharsets.UTF_8);
            final var url = new URL(urlString);
            return url.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildClipUrlFromClipId(@Nonnull final String clipId) {
        try {
            return new URL(TwitchService.BaseUrl + "/clip/" + clipId).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildVodUrlFromVodId(@Nonnull final String vodId) {
        try {
            return new URL(TwitchService.BaseUrl + "/videos/" + vodId).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildChannelTabUrlFromChannelNameAndTabType(
            @Nonnull final String channelName, @Nonnull final TwitchChannelTabLinkType type) {
        try {
            var typeString = switch (type) {
                case CLIPS -> "/clips";
                case VIDEOS -> "/videos";
                case LIVE -> "?_type=stream";
            };
            return new URL(TwitchService.BaseUrl + "/" + channelName + typeString).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String buildLiveKioskUrlFromCursor(final @Nullable String cursor) {
        return cursor == null
                ? "live?"
                : "live?" + cursor;
    }
}
