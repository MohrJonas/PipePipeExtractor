package org.schabi.newpipe.extractor.services.twitch.extractors;

import com.grack.nanojson.JsonParserException;

import org.jetbrains.annotations.NotNull;
import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.services.twitch.api.ThumbnailURLGenerator;
import org.schabi.newpipe.extractor.services.twitch.api.TwitchApi;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.clip.TwitchClipPlaybackResponseInner;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.DeliveryMethod;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public final class TwitchClipExtractor extends StreamExtractor {

    private TwitchClipPlaybackResponseInner[] clipResponse;

    public TwitchClipExtractor(final StreamingService service,
                               final LinkHandler linkHandler) {
        super(service, linkHandler);}

    @NotNull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        // FIXME
        return ThumbnailURLGenerator.NO_THUMBNAIL_URL;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            clipResponse = TwitchApi.getClipPlaybackToken(downloader, getId()).getData();
        } catch (JsonParserException e) {
            throw new IOException(e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        // FIXME
        return "???";
    }

    @Nonnull
    @Override
    public List<Image> getThumbnails() throws ParsingException {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        // FIXME
        return "???";
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        return "???";
    }

    @Override
    public List<VideoStream> getVideoStreams() throws IOException, ExtractionException {
        return Arrays.stream(clipResponse).map(res ->
                new VideoStream.Builder()
                        .setId(VideoStream.ID_UNKNOWN)
                        .setContent(res.clipUrl(), true)
                        .setDeliveryMethod(DeliveryMethod.PROGRESSIVE_HTTP)
                        .setResolution(res.clipWidth() + "x" + res.clipHeight())
                        .setIsVideoOnly(false)
                        .setMediaFormat(MediaFormat.MPEG_4)
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<AudioStream> getAudioStreams() throws IOException, ExtractionException {
        return Collections.emptyList();
    }

    @Override
    public List<VideoStream> getVideoOnlyStreams() throws IOException, ExtractionException {
        return Collections.emptyList();
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.POST_LIVE_STREAM;
    }
}
