package org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive;

public record TwitchNowLiveResponseEntry(String streamerName,
                                         String streamerLoginName,
                                         String streamTitle,
                                         int streamViewers,
                                         String thumbnailUrl,
                                         String gameName) { }
