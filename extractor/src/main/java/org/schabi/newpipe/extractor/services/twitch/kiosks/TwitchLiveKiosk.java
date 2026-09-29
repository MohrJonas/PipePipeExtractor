package org.schabi.newpipe.extractor.services.twitch.kiosks;

import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApi;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive.TwitchNowLiveResponse;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class TwitchLiveKiosk extends KioskExtractor<StreamInfoItem> {

    public static final String KIOSK_ID = "live";

    private TwitchNowLiveResponse response;

    public TwitchLiveKiosk(final StreamingService streamingService,
                           final ListLinkHandler linkHandler) {
        super(streamingService, linkHandler, KIOSK_ID);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            response = TwitchApi.getNowLiveInformation(downloader);
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return KIOSK_ID;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        response.getData()
                .forEach(searchEntry -> {
                    final var value = new StreamInfoItem(
                            getServiceId(),
                            TwitchUrlBuilder.buildStreamUrlFromChannelName(searchEntry.streamerName()),
                            searchEntry.streamTitle(),
                            StreamType.LIVE_STREAM
                    );
                    value.setViewCount(searchEntry.streamViewers());
                    value.setUploaderName(searchEntry.streamerName());
                    value.setUploaderUrl(TwitchUrlBuilder.buildStreamUrlFromChannelName(searchEntry.streamerName()));
                    value.setShortDescription(searchEntry.gameName());
                    value.setUploaderAvatarUrl(searchEntry.thumbnailUrl());
                    value.setThumbnailUrl(searchEntry.thumbnailUrl());
                });
        return new InfoItemsPage<>(
                response.getData()
                        .stream()
                        .map(liveEntry -> {
                            final var infoItem = new StreamInfoItem(
                                    getServiceId(),
                                    TwitchUrlBuilder.buildStreamUrlFromChannelName(liveEntry.streamerName()),
                                    liveEntry.streamTitle(),
                                    StreamType.LIVE_STREAM
                            );
                            infoItem.setViewCount(liveEntry.streamViewers());
                            infoItem.setUploaderName(liveEntry.streamerName());
                            infoItem.setUploaderUrl(TwitchUrlBuilder.buildStreamUrlFromChannelName(liveEntry.streamerName()));
                            infoItem.setShortDescription(liveEntry.gameName());
                            infoItem.setUploaderAvatarUrl(liveEntry.thumbnailUrl());
                            infoItem.setThumbnailUrl(liveEntry.thumbnailUrl());
                            return infoItem;
                        })
                        .collect(Collectors.toList()),
                null,
                Collections.emptyList());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(Page page) throws IOException, ExtractionException {
        return getInitialPage();
    }
}
