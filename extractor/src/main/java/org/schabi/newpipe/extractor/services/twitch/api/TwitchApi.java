package org.schabi.newpipe.extractor.services.twitch.api;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.services.twitch.StringUtils;
import org.schabi.newpipe.extractor.services.twitch.TwitchUtils;
import org.schabi.newpipe.extractor.services.twitch.data.Resolution;
import org.schabi.newpipe.extractor.services.twitch.data.TwitchVideoStream;
import org.schabi.newpipe.extractor.services.twitch.data.api.TwitchResponseParser;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchSideNavResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.channel.TwitchChannelResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip.TwitchClipPlaybackResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip.TwitchClipResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip.TwitchVideoPlayerMediaSessionManagerResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive.TwitchNowLiveResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.playbackToken.TwitchStreamPlaybackTokenResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.playbackToken.TwitchVodPlaybackTokenResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.TwitchSearchResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.stream.TwitchStreamResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.vod.TwitchVodResponse;
import org.schabi.newpipe.extractor.services.twitch.graphql.TwitchGQLTemplates;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public final class TwitchApi {

    private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
    private static final String TWITCH_QGL_URL = "https://gql.twitch.tv/gql";
    private static final String TWITCH_USHER_URL = "https://usher.ttvnw.net";

    private static final Map<String, List<String>> DEFAULT_HEADERS = Map.of("Client-ID", List.of(CLIENT_ID));

    public static TwitchSearchResponse getSearchResponse(final Downloader downloader, final String query) throws IOException, ReCaptchaException, JsonParserException {
        final var requestId = UUID.randomUUID().toString();
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getSearchResult(requestId, query)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.array().from(rawResponse.responseBody()).getObject(0), TwitchSearchResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchNowLiveResponse getNowLiveInformation(final Downloader downloader) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getNowLive()));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.object().from(rawResponse.responseBody()), TwitchNowLiveResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchStreamResponse getStreamInformation(final Downloader downloader, final String channelName) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getStream(channelName)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.object().from(rawResponse.responseBody()), TwitchStreamResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchStreamPlaybackTokenResponse getPlaybackToken(final Downloader downloader, final String channelName) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getPlaybackAccessTokenTemplate(channelName)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.object().from(rawResponse.responseBody()), TwitchStreamPlaybackTokenResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchVideoStream[] getM3U8PlaybackUrl(final Downloader downloader, final String channelName, final String playbackTokenSignature, final String playbackTokenValue) throws IOException, ReCaptchaException {
        final var masterM3U8Url = TWITCH_USHER_URL + "/api/v2/channel/hls/" + channelName + ".m3u8?allow_source=true&allow_audio_only=false&allow_spectre=true&p=1003155&platform=web&player=twitchweb&supported_codecs=av1,h265,h264&playlist_include_framerate=false&sig=" + playbackTokenSignature + "&token=" + URLEncoder.encode(playbackTokenValue, Charset.defaultCharset());
        final var response = downloader.get(masterM3U8Url);
        final var content = response.responseBody();
        final var lineQueue = new ArrayDeque<>(List.of(content.split("\n")));
        final var commentBuffer = new ArrayList<String>();
        final var streams = new ArrayList<TwitchVideoStream>();
        while (!lineQueue.isEmpty()) {
            final var line = lineQueue.pop();
            if (line.startsWith("#")) commentBuffer.add(line);
            else {
                final var resolutionPattern = Pattern.compile("RESOLUTION=(\\d+)x(\\d+)");
                Resolution resolution = null;
                for (final var bufferedLine : commentBuffer) {
                    final var matcher = resolutionPattern.matcher(bufferedLine);
                    if (matcher.find()) resolution = new Resolution(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2))
                    );
                }
                if (resolution != null)
                    streams.add(new TwitchVideoStream(
                            resolution,
                            line
                    ));
                commentBuffer.clear();
            }
        }
        return streams.toArray(TwitchVideoStream[]::new);
    }

    public static TwitchChannelResponse getTwitchChannel(final Downloader downloader, final String channelName) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getChannel(channelName)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(createJsonObjectFromArray(JsonParser.array().from(rawResponse.responseBody())), TwitchChannelResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchVodResponse getTwitchVods(final Downloader downloader, final String channelName) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getVods(channelName)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.array().from(rawResponse.responseBody()).getObject(0), TwitchVodResponse.class);
        response.ensureSuccess();
        return response;
    }

    private static JsonObject createJsonObjectFromArray(final JsonArray array) {
        var totalDuration = 0L;
        final var data = new JsonObject();
        final var errors = new LinkedList<>();
        for (var i = 0; i < array.size(); i++) {
            final var arrayElement = (JsonObject) array.get(i);
            data.put("synthetic-" + i, arrayElement.getObject("data"));
            totalDuration += arrayElement.getObject("extensions").getLong("durationMilliseconds");
            if (arrayElement.has("errors"))
                errors.addAll(arrayElement.getArray("errors"));
        }
        final var fullObject = new JsonObject();
        fullObject.put("data", data);
        final var fullExtensions = new JsonObject();
        fullExtensions.put("durationMilliseconds", totalDuration);
        fullObject.put("extensions", fullExtensions);
        if (!errors.isEmpty())
            fullObject.put("errors", errors);
        return fullObject;
    }

    public static TwitchClipResponse getTwitchClips(final Downloader downloader, final String channelName) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getClips(channelName)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.array().from(rawResponse.responseBody()).getObject(0), TwitchClipResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchClipPlaybackResponse getClipPlaybackToken(final Downloader downloader, final String clipSlug) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getClipPlayback(clipSlug)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.array().from(rawResponse.responseBody()).getObject(0), TwitchClipPlaybackResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchVodPlaybackTokenResponse getVodPlaybackToken(final Downloader downloader, final String vodId) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getVodPlaybackTokenTemplate(vodId)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.object().from(rawResponse.responseBody()), TwitchVodPlaybackTokenResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchVideoStream[] getM3U8VodPlaybackUrl(final Downloader downloader, final String vodId, final String playbackTokenSignature, final String playbackTokenValue, final String sessionId) throws IOException, ReCaptchaException {
        final var masterM3U8Url = TWITCH_USHER_URL + "/vod/v2/" + vodId + ".m3u8?acmb=eyJBcHBWZXJzaW9uIjoiMTFhYmE4MTYtZThlMi00M2MyLWJmOWUtNTNkMTNiM2EyYWEyIiwiQ2xpZW50QXBwIjoid2ViIn0%3D&allow_source=true&enable_score=true&include_unavailable=false&lang=en&multigroup_video=false&p=4876112&platform=web&player_backend=mediaplayer&play_session_id=" + sessionId + "&player_version=1.50.0-rc.4&playlist_include_framerate=true&reassignments_supported=true&sig=" + playbackTokenSignature + "&token=" + URLEncoder.encode(playbackTokenValue, Charset.defaultCharset());
        final var response = downloader.get(masterM3U8Url);
        final var content = response.responseBody();
        final var lineQueue = new ArrayDeque<>(List.of(content.split("\n")));
        final var commentBuffer = new ArrayList<String>();
        final var streams = new ArrayList<TwitchVideoStream>();
        while (!lineQueue.isEmpty()) {
            final var line = lineQueue.pop();
            if (line.startsWith("#")) commentBuffer.add(line);
            else {
                final var resolutionPattern = Pattern.compile("RESOLUTION=(\\d+)x(\\d+)");
                Resolution resolution = null;
                for (final var bufferedLine : commentBuffer) {
                    final var matcher = resolutionPattern.matcher(bufferedLine);
                    if (matcher.find()) resolution = new Resolution(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2))
                    );
                }
                if (resolution != null)
                    streams.add(new TwitchVideoStream(
                            resolution,
                            line
                    ));
                commentBuffer.clear();
            }
        }
        return streams.toArray(TwitchVideoStream[]::new);
    }

    public static TwitchVideoPlayerMediaSessionManagerResponse getTwitchVideoPlayerMediaSessionManager(@Nonnull final Downloader downloader, @Nonnull final String vodId) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getVideoPlayerMediaSessionManagerTemplate(vodId)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.object().from(rawResponse.responseBody()), TwitchVideoPlayerMediaSessionManagerResponse.class);
        response.ensureSuccess();
        return response;
    }

    public static TwitchSideNavResponse getTwitchSideNavResponse(@Nonnull final Downloader downloader, @Nonnull final String channelName) throws IOException, ReCaptchaException, JsonParserException {
        final var rawResponse = downloader.post(TWITCH_QGL_URL, DEFAULT_HEADERS, StringUtils.stringToBytes(TwitchGQLTemplates.getSideNavTemplate(channelName)));
        if (!TwitchUtils.isSuccessfulResponseCode(rawResponse.responseCode()))
            throw new ResponseCodeIsNotSuccessException(rawResponse.responseCode());
        final var response = TwitchResponseParser.parseFromJson(JsonParser.object().from(rawResponse.responseBody()), TwitchSideNavResponse.class);
        response.ensureSuccess();
        return response;
    }
}
