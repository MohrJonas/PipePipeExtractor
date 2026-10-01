package org.schabi.newpipe.extractor.services.twitch.linkHandlers;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;

import java.util.List;

public class TwitchLiveKioskLinkHandlerFactory extends ListLinkHandlerFactory {

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException, UnsupportedOperationException {
        return TwitchUrlBuilder.buildLiveKioskUrlFromCursor(null);
    }

    @Override
    public String getId(String url) throws ParsingException, UnsupportedOperationException {
        return "live";
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        try {
            TwitchUrlParser.parseLiveKioskCursorFromKioskUrl(url);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
