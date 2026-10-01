package org.schabi.newpipe.extractor.services.twitch.extractors;

import com.grack.nanojson.JsonParserException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ChannelTabs;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.search.filter.Filter;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApiClient;
import org.schabi.newpipe.extractor.services.twitch.data.TwitchChannelTabLinkType;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.channel.TwitchChannelResponseInner;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.vod.TwitchVodResponseInner;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class TwitchChannelExtractor extends ChannelExtractor {

    @Nonnull
    private final TwitchApiClient apiClient;
    private TwitchChannelResponseInner channelResponse;
    private TwitchVodResponseInner[] response;

    public TwitchChannelExtractor(final @Nonnull StreamingService service,
                                  final @Nonnull ListLinkHandler linkHandler,
                                  final @Nonnull TwitchApiClient apiClient) {
        super(service, linkHandler);
        this.apiClient = apiClient;
    }


    @NotNull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        final var collector = new StreamInfoItemsCollector(getServiceId());
        for (final var vod : response) {
            collector.commit(new StreamInfoItemExtractor() {
                @Override
                public StreamType getStreamType() throws ParsingException {
                    return StreamType.POST_LIVE_STREAM;
                }

                @Override
                public long getDuration() throws ParsingException {
                    return vod.getVodLength();
                }

                @Override
                public long getViewCount() throws ParsingException {
                    return vod.getVodViewerCount();
                }

                @Override
                public String getUploaderName() throws ParsingException {
                    return "";
                }

                @Nullable
                @Override
                public String getTextualUploadDate() throws ParsingException {
                    return null;
                }

                @Nullable
                @Override
                public DateWrapper getUploadDate() throws ParsingException {
                    return new DateWrapper(OffsetDateTime.parse(vod.getUploadDateTimeString()));
                }

                @Override
                public String getName() throws ParsingException {
                    return vod.getVodTitle();
                }

                @Override
                public String getUrl() throws ParsingException {
                    return TwitchUrlBuilder.buildVodUrlFromVodId(vod.getVodId());
                }

                @Override
                public String getThumbnailUrl() throws ParsingException {
                    return vod.getVodThumbnailUrl();
                }
            });
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(Page page) throws IOException, ExtractionException {
        return getInitialPage();
    }

    @Override
    public String getBannerUrl() throws ParsingException {
        return channelResponse.channelBannerUrl();
    }

    @Override
    public String getAvatarUrl() throws ParsingException {
        return channelResponse.streamerAvatarUrl();
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        return channelResponse.followerCount();
    }

    @Override
    public String getDescription() throws ParsingException {
        return channelResponse.streamerDescription();
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return channelResponse.isPartner();
    }

    @Nonnull
    @Override
    public List<ListLinkHandler> getTabs() throws ParsingException {
        final var list = new ArrayList<ListLinkHandler>();
        list.add(new ListLinkHandler(
                getUrl(),
                TwitchUrlBuilder.buildChannelTabUrlFromChannelNameAndTabType(getId(), TwitchChannelTabLinkType.LIVE),
                getId(),
                Collections.singletonList(new FilterItem(
                        Filter.ITEM_IDENTIFIER_UNKNOWN,
                        ChannelTabs.LIVESTREAMS
                )),
                null
        ));
        list.add(new ListLinkHandler(
                getUrl(),
                TwitchUrlBuilder.buildChannelTabUrlFromChannelNameAndTabType(getId(), TwitchChannelTabLinkType.VIDEOS),
                getId(),
                Collections.singletonList(new FilterItem(
                        Filter.ITEM_IDENTIFIER_UNKNOWN,
                        ChannelTabs.VIDEOS
                )),
                null
        ));
        list.add(new ListLinkHandler(
                getUrl(),
                TwitchUrlBuilder.buildChannelTabUrlFromChannelNameAndTabType(getId(), TwitchChannelTabLinkType.CLIPS),
                getId(),
                Collections.singletonList(new FilterItem(
                        Filter.ITEM_IDENTIFIER_UNKNOWN,
                        ChannelTabs.SHORTS
                )),
                null
        ));
        return list;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            final var channelName = TwitchUrlParser.parseChannelNameFromChannelUrl(getUrl());
            channelResponse = apiClient.getTwitchChannel(downloader, channelName).getData();
            response = apiClient.getTwitchVods(downloader, channelName).getData();
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return channelResponse.channelName();
    }
}
