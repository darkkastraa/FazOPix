package com.example.lojafazopix;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImgurResponse {
    @SerializedName("data")
    public Data data;
    @SerializedName("success")
    public boolean success;
    @SerializedName("status")
    public int status;

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public Object description; // Pode ser null
        @SerializedName("datetime")
        public int datetime;
        @SerializedName("type")
        public String type;
        @SerializedName("animated")
        public boolean animated;
        @SerializedName("width")
        public int width;
        @SerializedName("height")
        public int height;
        @SerializedName("size")
        public int size;
        @SerializedName("views")
        public int views;
        @SerializedName("bandwidth")
        public long bandwidth;
        @SerializedName("vote")
        public Object vote; // Pode ser null
        @SerializedName("favorite")
        public boolean favorite;
        @SerializedName("nsfw")
        public Object nsfw; // Pode ser null
        @SerializedName("section")
        public Object section; // Pode ser null
        @SerializedName("account_url")
        public Object account_url; // Pode ser null
        @SerializedName("account_id")
        public Object account_id; // Pode ser null
        @SerializedName("is_ad")
        public boolean is_ad;
        @SerializedName("in_most_viral")
        public boolean in_most_viral;
        @SerializedName("has_sound")
        public boolean has_sound;
        @SerializedName("tags")
        public List<Object> tags; // Pode ser vazia
        @SerializedName("ad_type")
        public int ad_type;@SerializedName("ad_url")
        public String ad_url;
        @SerializedName("edited")
        public String edited;
        @SerializedName("in_gallery")
        public boolean in_gallery;
        @SerializedName("deletehash")
        public String deletehash;
        @SerializedName("name")
        public String name;
        @SerializedName("link")
        public String link; // URL da imagem


        public String getLink() { return link; }
    }
    public Data getData() {
        return data;
    }
}
