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
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApiClient;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive.TwitchNowLiveResponse;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class TwitchLiveKiosk extends KioskExtractor<StreamInfoItem> {

    public static final String KIOSK_ID = "live";
    private static final int EntriesPerPage = 25;
    @Nonnull
    private final TwitchApiClient apiClient;
    private TwitchNowLiveResponse response;

    public TwitchLiveKiosk(final @Nonnull StreamingService streamingService,
                           final @Nonnull ListLinkHandler linkHandler,
                           final @Nonnull TwitchApiClient apiClient) {
        super(streamingService, linkHandler, KIOSK_ID);
        this.apiClient = apiClient;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        populateData(downloader, getUrl());
    }

    private void populateData(@Nonnull final Downloader downloader, @Nonnull final String url) throws IOException, ExtractionException {
        try {
            final var cursor = TwitchUrlParser.parseLiveKioskCursorFromKioskUrl(url);
            response = apiClient.getNowLiveInformation(downloader, EntriesPerPage, cursor);
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
        return new InfoItemsPage<>(
                response.getData().liveEntries()
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
//                This kiosk is theoretically set up for pagination but since I see no easy way to get around
//                Twitch's /integrity endpoint we have to see if this will work any day
//                response.getData().hasMoreEntries()
//                ? new Page(TwitchUrlBuilder.buildLiveKioskUrlFromCursor(response.getData().cursor()))
//                : null,
                Collections.emptyList());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(Page page) throws IOException, ExtractionException {
        populateData(getDownloader(), page.getUrl());
        return getInitialPage();
    }
}
