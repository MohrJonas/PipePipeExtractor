package org.schabi.newpipe.extractor.services.twitch.api;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchDirectoryResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchDirectoryResponseEntry;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchDirectoryResponseInner;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive.TwitchNowLiveResponseEntry;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive.TwitchNowLiveResponseInner;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AuthorizedTwitchApiClient extends TwitchBaseApiClient {

    @Nullable
    private final String helixApiToken;

    public AuthorizedTwitchApiClient(@Nullable final String helixApiToken) {
        this.helixApiToken = helixApiToken;
    }

    public TwitchNowLiveResponseInner getNowLiveInformation(@Nonnull final Downloader downloader, @Nullable final String cursor) throws JsonParserException, IOException, ReCaptchaException {
        return performRawRequest(downloader, HttpRequestType.GET,
                _ -> {
                    var url = TwitchApiConstants.TWITCH_HELIX_URL + "/streams";
                    if(cursor != null)
                        url += "?after=" + cursor;
                    return url;
                },
                _ -> null,
                _ -> TwitchApiHelper.buildHelixRequestHeaders(Objects.requireNonNull(helixApiToken), TwitchApiConstants.HELIX_CLIENT_ID),
                response -> {
                    final var obj = TwitchJsonHelper.parseJsonObjectFromString(response.responseBody());
                    final var data = obj.getArray("data");
                    final var pagination = obj.getObject("pagination");

                    return new TwitchNowLiveResponseInner(
                        !pagination.isEmpty(),
                        pagination.getString("cursor"),
                        data.stream().map(o -> {
                            final var innerObj = (JsonObject)o;
                            return new TwitchNowLiveResponseEntry(
                                innerObj.getString("user_name"),
                                    innerObj.getString("user_login"),
                                    innerObj.getString("title"),
                                    innerObj.getInt("viewer_count"),
                                    innerObj.getString("thumbnail_url")
                                        .replace("{width}", "440")
                                        .replace("{height}", "248"),
                                    innerObj.getString("game_name")
                            );
                        }).toList()
                    );
                }
        );
    }

    public TwitchDirectoryResponseInner GetDirectoryInformation(@Nonnull final Downloader downloader, @Nonnull final String directoryName, @Nonnull final String categoryId, @Nullable final String cursor) throws JsonParserException, IOException, ReCaptchaException {
        return performRawRequest(downloader, HttpRequestType.GET,
                _ -> {
                    var url = TwitchApiConstants.TWITCH_HELIX_URL + "/streams?game_id=" + categoryId;
                    if(cursor != null)
                        url += "&after=" + cursor;
                    return url;
                },
                _ -> null,
                _ -> TwitchApiHelper.buildHelixRequestHeaders(Objects.requireNonNull(helixApiToken), TwitchApiConstants.HELIX_CLIENT_ID),
                response -> {
                    final var obj = TwitchJsonHelper.parseJsonObjectFromString(response.responseBody());
                    final var data = obj.getArray("data");
                    final var pagination = obj.getObject("pagination");

                    return new TwitchDirectoryResponseInner(
                            !pagination.isEmpty(),
                            pagination.getString("cursor"),
                            directoryName,
                            categoryId,
                            data.stream().map(o -> {
                                final var innerObj = (JsonObject)o;
                                return new TwitchDirectoryResponseEntry(
                                        innerObj.getString("title"),
                                        innerObj.getString("game_id"),
                                        innerObj.getString("user_name"),
                                        innerObj.getString("user_login"),
                                        innerObj.getString("thumbnail_url")
                                                .replace("{width}", "440")
                                                .replace("{height}", "248"),
                                        // This does not exist in the query. We could do another query here but that would
                                        // dramatically increase load times with very litte use
                                        false,
                                        innerObj.getInt("viewer_count")
                                );
                            }).toList()
                    );
                }
        );
    }
}