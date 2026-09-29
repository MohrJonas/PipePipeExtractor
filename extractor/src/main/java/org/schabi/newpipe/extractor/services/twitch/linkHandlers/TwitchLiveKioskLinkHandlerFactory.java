package org.schabi.newpipe.extractor.services.twitch.linkHandlers;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;

import java.util.List;

public class TwitchLiveKioskLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final String KIOSK_TYPE = "live";

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException, UnsupportedOperationException {
        return id;
    }

    @Override
    public String getId(String url) throws ParsingException, UnsupportedOperationException {
        return url;
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        return url.equals(KIOSK_TYPE);
    }
}
