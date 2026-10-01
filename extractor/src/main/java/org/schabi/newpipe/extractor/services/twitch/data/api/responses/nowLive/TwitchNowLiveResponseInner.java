package org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive;

import java.util.List;

import javax.annotation.Nonnull;

public record TwitchNowLiveResponseInner(
        boolean hasMoreEntries,
        @Nonnull String cursor,
        List<TwitchNowLiveResponseEntry> liveEntries
) {
}
