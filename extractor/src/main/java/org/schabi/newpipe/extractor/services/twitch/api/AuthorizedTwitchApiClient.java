package org.schabi.newpipe.extractor.services.twitch.api;

import javax.annotation.Nullable;

public final class AuthorizedTwitchApiClient extends TwitchBaseApiClient {

    @Nullable
    private final String helixApiToken;

    public AuthorizedTwitchApiClient(@Nullable final String helixApiToken) {
        this.helixApiToken = helixApiToken;
    }
}