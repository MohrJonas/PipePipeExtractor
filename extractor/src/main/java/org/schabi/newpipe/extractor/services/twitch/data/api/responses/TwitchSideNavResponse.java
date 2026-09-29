package org.schabi.newpipe.extractor.services.twitch.data.api.responses;

import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.services.twitch.data.api.TwitchExtensionsData;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TwitchSideNavResponse extends TwitchBaseResponse<TwitchSideNavResponseInner[]> {

    public TwitchSideNavResponse(@Nullable String[] errors, @Nonnull TwitchExtensionsData extensions, @Nonnull JsonObject data) {
        super(errors, extensions, data);
    }

    @Override
    protected TwitchSideNavResponseInner[] ParseData(JsonObject data) {
        var edges = data.getObject("sideNav").getObject("sections").getArray("edges");
        return Stream.concat(
                edges.getObject(0).getObject("node").getObject("content").getArray("edges").stream(),
                edges.getObject(1).getObject("node").getObject("content").getArray("edges").stream()
        ).map(ob -> {
            var obj = (JsonObject) ob;
            var node = obj.getObject("node");
            var broadcaster = node.getObject("broadcaster");
            var broadcastSettings = broadcaster.getObject("broadcastSettings");
            return new TwitchSideNavResponseInner(
                    broadcaster.getString("displayName"),
                    broadcaster.getString("login"),
                    broadcastSettings.getString("title"),
                    node.getInt("viewersCount")
            );
        }).toArray(TwitchSideNavResponseInner[]::new);
    }
}
