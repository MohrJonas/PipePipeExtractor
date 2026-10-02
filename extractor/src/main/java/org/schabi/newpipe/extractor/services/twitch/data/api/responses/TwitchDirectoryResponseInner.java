package org.schabi.newpipe.extractor.services.twitch.data.api.responses;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.Nonnull;

public record TwitchDirectoryResponseInner(boolean hasMoreEntries,
                                           @Nullable String cursor,
                                           @Nonnull String directoryName,
                                           @Nonnull String gameId,
                                           @Nonnull List<TwitchDirectoryResponseEntry> entries) {
}
