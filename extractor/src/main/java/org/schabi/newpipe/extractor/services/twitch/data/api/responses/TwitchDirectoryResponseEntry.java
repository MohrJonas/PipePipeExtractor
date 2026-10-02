package org.schabi.newpipe.extractor.services.twitch.data.api.responses;

import javax.annotation.Nonnull;

public record TwitchDirectoryResponseEntry(@Nonnull String title,
                                           @Nonnull String id,
                                           @Nonnull String streamerName,
                                           @Nonnull String loginName,
                                           @Nonnull String profileImageUrl,
                                           boolean isPartner,
                                           int viewCount) {}