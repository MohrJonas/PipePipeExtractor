package org.schabi.newpipe.extractor.services.twitch.extractors;

import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelTabExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.api.ThumbnailURLGenerator;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApi;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.stream.TwitchStreamResponseInner;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public final class TwitchChannelStreamExtractor extends ChannelTabExtractor {

    private TwitchStreamResponseInner response;

    public TwitchChannelStreamExtractor(@Nonnull StreamingService service, @Nonnull ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        if (response.streamTitle() == null)
            return new InfoItemsPage<>(Collections.emptyList(), null, Collections.emptyList());
        var item = new StreamInfoItem(
                getServiceId(),
                TwitchUrlBuilder.buildStreamUrlFromChannelName(response.streamerName()),
                response.streamTitle(),
                StreamType.LIVE_STREAM
        );
        item.setUploaderName(response.streamerName());
        item.setViewCount(response.viewerCount());
        item.setThumbnailUrl(ThumbnailURLGenerator.getThumbnailURLForStream(response.streamerName()));
        return new InfoItemsPage<>(List.of(item), null, Collections.emptyList());
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(Page page) throws IOException, ExtractionException {
        return getInitialPage();
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            response = TwitchApi.getStreamInformation(downloader, getId()).getData();
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }
}
