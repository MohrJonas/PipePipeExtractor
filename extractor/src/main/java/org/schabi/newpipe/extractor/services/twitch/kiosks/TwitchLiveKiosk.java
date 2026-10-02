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
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.nowLive.TwitchNowLiveResponseInner;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class TwitchLiveKiosk extends KioskExtractor<StreamInfoItem> {

    public static final String KIOSK_ID = "live";
    @Nonnull
    private final TwitchApiClient apiClient;
    private TwitchNowLiveResponseInner response;

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
            if(apiClient.getApiSettings().shouldUseHelixForKiosk()) {
                final var cursor = TwitchUrlParser.parseLiveKioskCursorFromKioskUrl(url);
                response = apiClient.getAuthorized().getNowLiveInformation(downloader, cursor);
            }

            else
                response = apiClient.getUnauthorized().getNowLiveInformation(downloader).getData();
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
                response.liveEntries()
                        .stream()
                        .map(liveEntry -> {
                            final var infoItem = new StreamInfoItem(
                                    getServiceId(),
                                    TwitchUrlBuilder.buildStreamUrlFromChannelName(liveEntry.streamerLoginName()),
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
                        apiClient.getApiSettings().shouldUseHelixForKiosk() && response.hasMoreEntries()
                    ? new Page(TwitchUrlBuilder.buildLiveKioskUrlFromCursor(response.cursor()))
                    : null,
                Collections.emptyList());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(Page page) throws IOException, ExtractionException {
        populateData(getDownloader(), page.getUrl());
        return getInitialPage();
    }
}
