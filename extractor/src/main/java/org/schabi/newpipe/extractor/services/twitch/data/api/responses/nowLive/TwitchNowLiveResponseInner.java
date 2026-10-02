package org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.Nonnull;

public record TwitchNowLiveResponseInner(
        boolean hasMoreEntries,
        @Nullable String cursor,
        List<TwitchNowLiveResponseEntry> liveEntries
) {
}
