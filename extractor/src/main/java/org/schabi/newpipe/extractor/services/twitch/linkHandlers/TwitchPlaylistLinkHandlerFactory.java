package org.schabi.newpipe.extractor.services.twitch.linkHandlers;

import org.apache.commons.lang3.tuple.Triple;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlBuilder;
import org.schabi.newpipe.extractor.services.twitch.TwitchUrlParser;

import java.util.List;

public class TwitchPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {

    private Triple<String, String, String> triple;

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException {
        final var parts = id.split("\\|\\|");
        return TwitchUrlBuilder.buildCategoryUrlFromCategoryName(
                parts[0],
                parts[1],
                parts.length == 2 ? null : parts[2]
        );
    }

    @Override
    public String getId(String url) throws ParsingException {
        final var triple = TwitchUrlParser.parseCategoryNameAndIdAndCursorFromCategoryUrl(url);
        var tripleString = triple.getLeft() + "||" + triple.getMiddle();
        if(triple.getRight() != null)
            tripleString += "||" + triple.getRight();
        return  tripleString;
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        try {
            triple = TwitchUrlParser.parseCategoryNameAndIdAndCursorFromCategoryUrl(url);
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
    }
}
