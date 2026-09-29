package org.schabi.newpipe.extractor.services.twitch.extractors;

import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.Image;
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
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApi;
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

    private TwitchClipResponseInner[] response;

    public TwitchChannelClipExtractor(final StreamingService service, final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return new InfoItemsPage<>(Arrays.stream(response).map(res -> {
            final var item = new StreamInfoItem(getServiceId(), TwitchUrlBuilder.buildClipUrlFromClipId(res.getClipId()), res.getClipTitle(), StreamType.NONE);
            item.setThumbnailUrl(res.getClipThumbnailUrl());
            try {
                item.setUploaderName(getId() + " + " + res.getClipperName());
            } catch (ParsingException e) {
                item.setUploaderName("");
            }
            item.setDuration(res.getClipLength());
            item.setViewCount(res.getClipViewerCount());
            item.setShortFormContent(true);
            item.setShortDescription("(" + res.getClipperName() + "), " + res.getGameName());
            item.setUploadDate(new DateWrapper(OffsetDateTime.parse(res.getUploadDateTimeString())));
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
            response = TwitchApi.getTwitchClips(downloader, pair.getFirst()).getData();
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }
}
