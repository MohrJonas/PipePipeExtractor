package org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip;

import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.services.twitch.data.api.TwitchExtensionsData;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchBaseResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TwitchVideoPlayerMediaSessionManagerResponse extends TwitchBaseResponse<TwitchVideoPlayerMediaSessionManagerResponseInner> {
    public TwitchVideoPlayerMediaSessionManagerResponse(@Nullable String[] errors, @Nonnull TwitchExtensionsData extensions, @Nonnull JsonObject data) {
        super(errors, extensions, data);
    }

    @Override
    protected TwitchVideoPlayerMediaSessionManagerResponseInner ParseData(JsonObject data) {
        final var video = data.getObject("video");
        final var owner = video.getObject("owner");
        return new TwitchVideoPlayerMediaSessionManagerResponseInner(
            video.getString("title"),
            owner.getString("displayName"),
            owner.getString("login"),
            owner.getString("profileImageURL")
        );
    }
}
