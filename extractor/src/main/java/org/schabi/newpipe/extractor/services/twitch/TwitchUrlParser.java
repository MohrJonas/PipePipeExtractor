package org.schabi.newpipe.extractor.services.twitch;

import org.schabi.newpipe.extractor.services.twitch.data.TwitchChannelTabLinkType;
import org.schabi.newpipe.extractor.utils.Pair;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TwitchUrlParser {

    private static void ensureIsCorrectBaseUrl(@Nonnull final String urlString) {
        Assertions.assertThat(() -> Utils.removeMAndWWWFromUrl(urlString).startsWith(TwitchService.BaseUrl));
    }

    public static @Nonnull String parseQueryFromSearchUrl(@Nonnull final String urlString) {
        ensureIsCorrectBaseUrl(urlString);
        try {
            final var url = new URL(urlString);
            return Objects.requireNonNull(Utils.getQueryValue(url, "term"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nonnull String parseChannelNameFromChannelOrStreamUrl(@Nonnull final String urlString, @Nonnull final String trailingIdentifier) {
        ensureIsCorrectBaseUrl(urlString);
        try {
            final var url = new URL(urlString);
            Assertions.assertThat(() ->
                    Objects.requireNonNull(Utils.getQueryValue(url, "_type")).equals(trailingIdentifier));
            return url.getPath().replaceFirst("/", "");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String parseChannelNameFromStreamUrl(@Nonnull final String urlString) {
        return parseChannelNameFromChannelOrStreamUrl(urlString, "stream");
    }

    public static @Nonnull String parseChannelNameFromChannelUrl(@Nonnull final String urlString) {
        return parseChannelNameFromChannelOrStreamUrl(urlString, "channel");
    }

    public static @Nonnull String parseClipIdFromClipUrl(@Nonnull final String urlString) {
        ensureIsCorrectBaseUrl(urlString);
        try {
            final var url = new URL(urlString);
            Assertions.assertThat(() -> url.getQuery() == null);
            final var path = url.getPath().replaceFirst("/", "");
            Assertions.assertThat(() -> !path.isEmpty());
            final var parts = path.split("/");
            Assertions.assertThat(() -> parts.length == 2);
            Assertions.assertThat(() -> parts[0].equals("clip"));
            return parts[1];
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull String parseVodIdFromVodUrl(@Nonnull final String urlString) {
        ensureIsCorrectBaseUrl(urlString);
        try {
            final var url = new URL(urlString);
            Assertions.assertThat(() -> url.getQuery() == null);
            final var path = url.getPath().replaceFirst("/", "");
            Assertions.assertThat(() -> !path.isEmpty());
            final var parts = path.split("/");
            Assertions.assertThat(() -> parts.length == 2);
            Assertions.assertThat(() -> parts[0].equals("videos"));
            return parts[1];
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nonnull Pair<String, TwitchChannelTabLinkType>
    parseChannelTabFromChannelUrl(@Nonnull final String urlString) {
        ensureIsCorrectBaseUrl(urlString);
        try {
            final var url = new URL(urlString);
            final var path = url.getPath().replaceFirst("/", "");
            Assertions.assertThat(() -> !path.isEmpty());
            final var parts = path.split("/");
            Assertions.assertThat(() -> parts.length == 1 || parts.length == 2);
            // TODO Check query part for ?_type=stream
            if (parts.length == 1)
                return new Pair<>(parts[0], TwitchChannelTabLinkType.LIVE);
            var type = switch (parts[1]) {
                case "clips" -> TwitchChannelTabLinkType.CLIPS;
                case "videos" -> TwitchChannelTabLinkType.VIDEOS;
                default -> throw new IllegalStateException("Unexpected value: " + path);
            };
            return new Pair<>(parts[0], type);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable String parseLiveKioskCursorFromKioskUrl(@Nonnull final String urlString) {
        Assertions.assertThat(() -> urlString.contains("?"));
        final var parts = urlString.split("\\?");
        Assertions.assertThat(() -> parts.length == 1 || parts.length == 2);
        Assertions.assertThat(() -> parts[0].equals("live"));
        return parts.length == 1 ? null : parts[1];
    }
}
