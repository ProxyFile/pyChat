package de.proxyfile.pychat.discord;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.Instant;

public class EmbedBuilder {

    private final JSONObject embed;
    private final JSONArray fields;

    public EmbedBuilder() {
        this.embed = new JSONObject();
        this.fields = new JSONArray();
        embed.put("fields", fields);
        embed.put("timestamp", Instant.now().toString());
    }

    public EmbedBuilder setTitle(String title) {
        embed.put("title", title);
        return this;
    }

    public EmbedBuilder setDescription(String description) {
        embed.put("description", description);
        return this;
    }

    public EmbedBuilder setColor(int color) {
        embed.put("color", color);
        return this;
    }

    public EmbedBuilder setThumbnail(String url) {
        JSONObject thumbnailObj = new JSONObject();
        thumbnailObj.put("url", url);
        embed.put("thumbnail", thumbnailObj);
        return this;
    }

    public EmbedBuilder setImage(String url) {
        JSONObject imageObj = new JSONObject();
        imageObj.put("url", url);
        embed.put("image", imageObj);
        return this;
    }

    public EmbedBuilder setFooter(String text, String iconUrl) {
        JSONObject footer = new JSONObject();
        footer.put("text", text);
        if (iconUrl != null) {
            footer.put("icon_url", iconUrl);
        }
        embed.put("footer", footer);
        return this;
    }

    public EmbedBuilder setFooter(String text) {
        return setFooter(text, null);
    }

    public EmbedBuilder addField(String name, String value, boolean inline) {
        JSONObject field = new JSONObject();
        field.put("name", name);
        field.put("value", value);
        field.put("inline", inline);
        fields.add(field);
        return this;
    }

    public JSONObject build() {
        return embed;
    }

    public JSONObject buildWebhookPayload() {
        JSONArray embedsArray = new JSONArray();
        embedsArray.add(embed);

        JSONObject payload = new JSONObject();
        payload.put("embeds", embedsArray);
        return payload;
    }

}
