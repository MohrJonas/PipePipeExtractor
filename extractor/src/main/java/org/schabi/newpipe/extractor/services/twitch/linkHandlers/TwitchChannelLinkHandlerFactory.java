package org.schabi.newpipe.extractor.services.twitch.linkHandlers;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;

import java.util.List;
import java.util.Objects;

public final class TwitchChannelLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(String url) throws ParsingException, UnsupportedOperationException {
        return TwitchUrlParser.parseChannelNameFromChannelUrl(url);
    }

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException, UnsupportedOperationException {
        return TwitchUrlBuilder.buildChannelUrlFromChannelName(id);
    }

    @Override
    public boolean onAcceptUrl(String urlString) throws ParsingException {
        try {
            var channelName = TwitchUrlParser.parseChannelNameFromChannelUrl(urlString);
            Objects.requireNonNull(channelName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
