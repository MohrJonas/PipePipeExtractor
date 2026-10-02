package org.schabi.newpipe.extractor.services.twitch.extractors;

import com.grack.nanojson.JsonParserException;

import org.jetbrains.annotations.NotNull;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApiClient;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchThumbnailURLGenerator;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchDirectoryResponseInner;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Nonnull;

public class TwitchPlaylistExtractor extends PlaylistExtractor {

    @Nonnull
    private final TwitchApiClient apiClient;

    private TwitchDirectoryResponseInner response;

    public TwitchPlaylistExtractor(@Nonnull final StreamingService service,
                                   @Nonnull final ListLinkHandler linkHandler,
                                   @Nonnull final TwitchApiClient apiClient) {
        super(service, linkHandler);
        this.apiClient = apiClient;
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        return "";
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return response.directoryName();
    }

    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        return "";
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Override
    public long getStreamCount() throws ParsingException {
        return PlaylistExtractor.ITEM_COUNT_UNKNOWN;
    }

    private void populateData(@Nonnull final Downloader downloader, @Nonnull final String url) throws IOException, ExtractionException {
        try {
            final var triple = TwitchUrlParser.parseCategoryNameAndIdAndCursorFromCategoryUrl(url);
            if(apiClient.getApiSettings().shouldUseHelixForCategories()) {
                response = apiClient.getAuthorized().GetDirectoryInformation(
                        downloader,
                        triple.getLeft(),
                        triple.getMiddle(),
                        triple.getRight()
                );
            }

            else
                response = apiClient.getUnauthorized().getTwitchCategory(downloader, triple.getLeft()).getData();
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }

    @NotNull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return new InfoItemsPage<>(
                response.entries().stream().map(directoryEntry -> {
                    final var streamItem = new StreamInfoItem(
                            getServiceId(),
                            TwitchUrlBuilder.buildStreamUrlFromChannelName(directoryEntry.loginName()),
                            directoryEntry.title(),
                            StreamType.VIDEO_STREAM
                    );
                    streamItem.setThumbnailUrl(TwitchThumbnailURLGenerator.getThumbnailURLForStream(directoryEntry.loginName()));
                    streamItem.setUploaderAvatarUrl(directoryEntry.profileImageUrl());
                    streamItem.setUploaderName(directoryEntry.streamerName());
                    streamItem.setShortFormContent(false);
                    streamItem.setUploaderUrl(TwitchUrlBuilder.buildChannelUrlFromChannelName(directoryEntry.loginName()));
                    streamItem.setUploaderVerified(directoryEntry.isPartner());
                    streamItem.setViewCount(directoryEntry.viewCount());
                    return streamItem;
                }).toList(),
                apiClient.getApiSettings().shouldUseHelixForCategories()
                ? new Page(TwitchUrlBuilder.buildCategoryUrlFromCategoryName(response.directoryName(), response.gameId(), response.cursor()))
                : null,
                Collections.emptyList()
        );
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(Page page) throws IOException, ExtractionException {
        populateData(getDownloader(), page.getUrl());
        return getInitialPage();
    }

    @Override
    public void onFetchPage(@NotNull Downloader downloader) throws IOException, ExtractionException {
        populateData(downloader, getUrl());
    }

    @NotNull
    @Override
    public String getName() throws ParsingException {
        return response.directoryName();
    }
}
