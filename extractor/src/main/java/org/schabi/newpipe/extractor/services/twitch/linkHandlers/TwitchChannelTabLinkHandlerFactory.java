package org.schabi.newpipe.extractor.services.twitch.linkHandlers;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;
import org.schabi.newpipe.extractor.services.twitch.data.TwitchChannelTabLinkType;

import java.util.List;

public class TwitchChannelTabLinkHandlerFactory extends ListLinkHandlerFactory {
    private TwitchChannelTabLinkType type;
    private String channelName;

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException {
        return TwitchUrlBuilder.buildChannelTabUrlFromChannelNameAndTabType(channelName, type);
    }

    @Override
    public String getId(String url) throws ParsingException {
        return TwitchUrlParser.parseChannelNameFromChannelUrl(url);
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        try {
            var tuple = TwitchUrlParser.parseChannelTabFromChannelUrl(url);
            channelName = tuple.getFirst();
            type = tuple.getSecond();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
