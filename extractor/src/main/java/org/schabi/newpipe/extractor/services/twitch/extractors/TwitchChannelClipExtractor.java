package org.schabi.newpipe.extractor.services.twitch.extractors;

import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelTabExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.twitch.Assertions;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApiClient;
import org.schabi.newpipe.extractor.services.twitch.data.TwitchChannelTabLinkType;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip.TwitchClipResponseInner;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public final class TwitchChannelClipExtractor extends ChannelTabExtractor {

    @Nonnull
    private final TwitchApiClient apiClient;
    private TwitchClipResponseInner[] response;

    public TwitchChannelClipExtractor(final @Nonnull StreamingService service,
                                      final @Nonnull ListLinkHandler linkHandler,
                                      final @Nonnull TwitchApiClient apiClient) {
        super(service, linkHandler);
        this.apiClient = apiClient;
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return new InfoItemsPage<>(Arrays.stream(response).map(res -> {
            final var item = new StreamInfoItem(getServiceId(), TwitchUrlBuilder.buildClipUrlFromClipId(res.clipId()), res.clipTitle(), StreamType.NONE);
            item.setThumbnailUrl(res.clipThumbnailUrl());
            try {
                item.setUploaderName(getId() + " + " + res.clipperName());
            } catch (ParsingException e) {
                item.setUploaderName("");
            }
            item.setDuration(res.clipLength());
            item.setViewCount(res.clipViewerCount());
            item.setShortFormContent(true);
            item.setShortDescription("(" + res.clipperName() + "), " + res.gameName());
            item.setUploadDate(new DateWrapper(OffsetDateTime.parse(res.uploadDateTimeString())));
            return item;
        }).collect(Collectors.toList()), null, Collections.emptyList());
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(Page page) throws IOException, ExtractionException {
        return getInitialPage();
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            final var pair = TwitchUrlParser.parseChannelTabFromChannelUrl(getUrl());
            Assertions.assertThat(() -> pair.getSecond() == TwitchChannelTabLinkType.CLIPS);
            response = apiClient.getUnauthorized().getTwitchClips(downloader, pair.getFirst()).getData();
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }
}
