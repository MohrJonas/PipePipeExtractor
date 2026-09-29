package org.schabi.newpipe.extractor.services.twitch.linkHandlers;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;

public class TwitchPlaylistLinkHandlerFactory extends LinkHandlerFactory {
    @Override
    public String getId(String url) throws ParsingException {
        return "";
    }

    @Override
    public String getUrl(String id) throws ParsingException {
        return "";
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        return false;
    }
}
