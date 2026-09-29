package org.schabi.newpipe.extractor.services.twitch.data.api.responses.search;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.services.twitch.api.ThumbnailURLGenerator;
import org.schabi.newpipe.extractor.services.twitch.data.api.TwitchExtensionsData;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.TwitchBaseResponse;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.types.TwitchSearchChannelResponseEntry;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.types.TwitchSearchGameResponseEntry;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.types.TwitchSearchStreamResponseEntry;
import org.schabi.newpipe.extractor.services.twitch.data.api.responses.search.types.TwitchSearchVodResponseEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TwitchSearchResponse extends TwitchBaseResponse<List<TwitchSearchBaseResponseEntry>> {

    public TwitchSearchResponse(@Nullable String[] errors, @Nonnull TwitchExtensionsData extensions, @Nonnull JsonObject data) {
        super(errors, extensions, data);
    }

    private static @Nonnull TwitchSearchChannelResponseEntry parseAsChannelEntry(JsonObject node) {
        final var item = node.getObject("item");
        return new TwitchSearchChannelResponseEntry(item.getString("displayName"), item.getString("description"), item.getString("profileImageURL"), item.getObject("roles").getBoolean("isPartner"), item.getObject("followers").getInt("totalCount"));
    }

    private static @Nonnull TwitchSearchStreamResponseEntry parseAsStreamEntry(JsonObject node) {
        final var item = node.getObject("item");
        final var stream = item.getObject("stream");
        return new TwitchSearchStreamResponseEntry(
                item.getString("displayName"),
                item.getObject("broadcastSettings").getString("title"),
                stream.getInt("viewersCount"),
                ThumbnailURLGenerator.getThumbnailURLForStream(item.getString("login")),
                stream.getObject("game").getString("name"),
                item.getString("profileImageURL")
        );
    }

    private static @Nonnull TwitchSearchVodResponseEntry parseAsVodEntry(JsonObject node) {
        final var item = node.getObject("item");
        return new TwitchSearchVodResponseEntry(
                item.getString("title"),
                item.getObject("owner").getString("displayName"),
                item.getInt("viewCount"),
                item.getInt("lengthSeconds"),
                item.getString("previewThumbnailURL"),
                item.getString("createdAt"),
                item.getString("id")
            );
    }

    private static @Nonnull TwitchSearchGameResponseEntry parseAsGameEntry(JsonObject node) {
        final var item = node.getObject("item");
        return new TwitchSearchGameResponseEntry(item.getString("displayName"), item.getString("boxArtURL"));
    }

    @Override
    protected List<TwitchSearchBaseResponseEntry> ParseData(JsonObject o) {
        final var searchFor = o.getObject("searchFor");
        final Optional<JsonArray> channels = searchFor.has("channels") ? Optional.of(searchFor.getObject("channels").getArray("edges")) : Optional.empty();
        final Optional<JsonArray> games = searchFor.has("games") ? Optional.of(searchFor.getObject("games").getArray("edges")) : Optional.empty();
        final Optional<JsonArray> vods = searchFor.has("videos") ? Optional.of(searchFor.getObject("videos").getArray("edges")) : Optional.empty();
        final var masterList = new LinkedList<TwitchSearchBaseResponseEntry>();
        channels.ifPresent(objects -> masterList.addAll(objects.stream().map(ob -> {
            final var obj = (JsonObject)ob;
            final var isLive = obj.getObject("item").getObject("stream").has("viewersCount");
            return isLive ? parseAsStreamEntry(obj) : parseAsChannelEntry(obj);
        }).toList()));
        games.ifPresent(objects ->
                masterList.addAll(objects.stream().map(ob -> TwitchSearchResponse.parseAsGameEntry((JsonObject) ob)).toList()));
        vods.ifPresent(objects ->
                masterList.addAll(objects.stream().map(ob -> TwitchSearchResponse.parseAsVodEntry((JsonObject) ob)).toList()));
        return masterList;
    }
}
