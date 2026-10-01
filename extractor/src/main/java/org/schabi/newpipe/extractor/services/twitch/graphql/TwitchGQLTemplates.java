package org.schabi.newpipe.extractor.services.twitch.graphql;

import javax.annotation.Nullable;

public final class TwitchGQLTemplates {
    private static final String STREAM_PLAYBACK_ACCESS_TOKEN_TEMPLATE = "{\"query\": \"{\\n" +
            "            streamPlaybackAccessToken(\\n" +
            "                channelName: \\\"%s\\\",\\n" +
            "                params: {\\n" +
            "                    platform: \\\"web\\\",\\n" +
            "                    playerBackend: \\\"mediaplayer\\\",\\n" +
            "                    playerType: \\\"site\\\"\\n" +
            "                }\\n" +
            "            )\\n" +
            "            {\\n" +
            "                value\\n" +
            "                signature\\n" +
            "            }\\n" +
            "        }\\n" +
            "\"}";

    private static final String STREAMER_TEMPLATE = "{\"query\": \"{\\n" +
            "  user(login: \\\"%s\\\") {\\n" +
            "    displayName\\n" +
            "    login\\n" +
            "    stream {\\n" +
            "      title\\n" +
            "      viewersCount\\n" +
            "    }\\n" +
            "  }\\n" +
            "}\"}";

    private static final String SEARCH_TEMPLATE = """
            [
                {
                    "operationName": "SearchResultsPage_SearchResults",
                    "variables": {
                        "requestID": "%s",
                        "query": "%s",
                        "platform": "web",
                        "options": { "targets": null, "shouldSkipDiscoveryControl": false }
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "a7c600111acc4d1b294eafa364600556227939e2ff88505faa73035b57a83b22"
                        }
                    }
                }
            ]""";

    private static final String NOW_LIVE_TEMPLATE = "{\"query\":\"{\\n" +
            "    streams(first: %d%s) {\\n" +
            "        edges {\\n" +
            "        cursor\\n" +
            "        node {\\n" +
            "            id\\n" +
            "            title\\n" +
            "            viewersCount\\n" +
            "            broadcaster {\\n" +
            "                displayName\\n" +
            "                login\\n" +
            "            }\\n" +
            "            game {\\n" +
            "                name\\n" +
            "            }\\n" +
            "        }\\n" +
            "        }\\n" +
            "        pageInfo {\\n" +
            "            hasNextPage\\n" +
            "        }\\n" +
            "    }\\n" +
            "}\"}";

    private static final String CHANNEL_TEMPLATE = """
            [
                {
                    "operationName": "HomeOfflineCarousel",
                    "variables": {
                        "channelLogin": "%s",
                        "includeTrailerUpsell": false,
                        "trailerUpsellVideoID": "601752619"
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "0409584bcabf718836bf330c29d0ac9d9a58f9674f7684bcbfce1a3e8dcf93b2"
                        }
                    }
                },
                {
                    "operationName": "ChannelAvatar",
                    "variables": {
                        "channelLogin": "%s"
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "db0e7b54c5e75fcf7874cafca2dacde646344cbbd1a80a2488a7953176c87a68"
                        }
                    }
                },
                {
                    "operationName": "ChannelShell",
                    "variables": {
                        "login": "%s"
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "fea4573a7bf2644f5b3f2cbbdcbee0d17312e48d2e55f080589d053aad353f11"
                        }
                    }
                }
            ]""";

    private static final String VOD_TEMPLATE = """
            [
                {
                    "operationName": "FilterableVideoTower_Videos",
                    "variables": {
                        "includePreviewBlur": false,
                        "limit": 30,
                        "channelOwnerLogin": "%s",
                        "broadcastType": null,
                        "videoSort": "TIME"
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "67004f7881e65c297936f32c75246470629557a393788fb5a69d6d9a25a8fd5f"
                        }
                    }
                }
            ]""";

    private static final String CLIP_TEMPLATE = """
            [
                {
                    "operationName": "ClipsCards__User",
                    "variables": {
                        "login": "%s",
                        "limit": 20,
                        "criteria": {
                            "filter": "ALL_TIME",
                            "shouldFilterByDiscoverySetting": true
                        },
                        "cursor": null
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "1cd671bfa12cec480499c087319f26d21925e9695d1f80225aae6a4354f23088"
                        }
                    }
                }
            ]""";

    private static final String CLIP_PLAYBACK_ACCESS_TOKEN = """
            [
                {
                    "operationName": "VideoAccessToken_Clip",
                    "variables": {
                        "platform": "web",
                        "slug": "%s"
                    },
                    "extensions": {
                        "persistedQuery": {
                            "version": 1,
                            "sha256Hash": "4f35f1ac933d76b1da008c806cd5546a7534dfaff83e033a422a81f24e5991b3"
                        }
                    }
                }
            ]""";

    private static final String VOD_PLAYBACK_ACCESS_TOKEN = """
            {
                "operationName": "PlaybackAccessToken",
                "variables": {
                    "isLive": false,
                    "login": "",
                    "isVod": true,
                    "vodID": "%s",
                    "playerType": "site",
                    "platform": "web"
                },
                "extensions": {
                    "persistedQuery": {
                        "version": 1,
                        "sha256Hash": "ed230aa1e33e07eebb8928504583da78a5173989fadfb1ac94be06a04f3cdbe9"
                    }
                }
            }""";

    private static final String MEDIA_SESSION_MANAGER_TEMPLATE = """
            {
                "operationName": "VideoPlayerMediaSessionManager",
                "variables": {
                    "clipSlug": "",
                    "isClip": false,
                    "isLive": false,
                    "isVodOrCollection": true,
                    "vodID": "%s"
                },
                "extensions": {
                    "persistedQuery": {
                        "version": 1,
                        "sha256Hash": "694c36677896425624f1293c9cb5aa4d08ed813993cf84c80d13d9380721fda2"
                    }
                }
            }""";

    private static final String SIDE_NAV_TEMPLATE = """
            {
                "operationName": "SideNav",
                "variables": {
                    "creatorAnniversariesFeature": false,
                    "withFreeformTags": false,
                    "input": {
                        "contextChannelName": "%s",\
                        "recommendationContext": {\
                            "categorySlug" : null,\
                            "channelName" : "%s",\
                            "clientApp" : "twilight",\
                            "lastCategorySlug" : null,\
                            "lastChannelName" : "%s",\
                            "location": "channel",\
                            "pageviewContent" : "similar_channels",\
                            "pageviewContentType" : null,\
                            "pageviewLocation" : "channel",\
                            "pageviewMedium" : "twitch_socialcolumn",\
                            "platform" : "web",\
                            "previousPageviewContent" : null,\
                            "previousPageviewContentType" : null,\
                            "previousPageviewLocation" : "channel",\
                            "previousPageviewMedium" : null,\
                            "referrerDomain": "www.twitch.tv",\
                            "viewportHeight": 1286,\
                            "viewportWidth": 2560\
                        }
                    }
                },
                "extensions": {
                    "persistedQuery": {
                        "version": 1,
                        "sha256Hash": "7984456538921edc68de00b6822e733fb31132d5f2e79818910caf8e7532a85b"
                    }
                }
            }""";

    public static String getSideNavTemplate(final String channelName) {
        return String.format(SIDE_NAV_TEMPLATE, channelName, channelName, channelName);
    }

    public static String getVideoPlayerMediaSessionManagerTemplate(final String vodId) {
        return String.format(MEDIA_SESSION_MANAGER_TEMPLATE, vodId);
    }

    public static String getPlaybackAccessTokenTemplate(final String channelName) {
        return String.format(STREAM_PLAYBACK_ACCESS_TOKEN_TEMPLATE, channelName);
    }

    public static String getStream(final String channelName) {
        return String.format(STREAMER_TEMPLATE, channelName);
    }

    public static String getSearchResult(final String requestId, final String query) {
        return String.format(SEARCH_TEMPLATE, requestId, query);
    }

    public static String getNowLive(final int count, @Nullable final String cursor) {
        return String.format(NOW_LIVE_TEMPLATE, count,
                cursor != null
                        ? ", after: " + "\\\"" + cursor + "\\\""
                        : ""
        );
    }

    public static String getChannel(final String channelName) {
        return String.format(CHANNEL_TEMPLATE, channelName, channelName, channelName);
    }

    public static String getVods(final String channelName) {
        return String.format(VOD_TEMPLATE, channelName);
    }

    public static String getClips(final String channelName) {
        return String.format(CLIP_TEMPLATE, channelName);
    }

    public static String getClipPlayback(final String clipSlug) {
        return String.format(CLIP_PLAYBACK_ACCESS_TOKEN, clipSlug);
    }

    public static String getVodPlaybackTokenTemplate(final String vodId) {
        return String.format(VOD_PLAYBACK_ACCESS_TOKEN, vodId);
    }
}
