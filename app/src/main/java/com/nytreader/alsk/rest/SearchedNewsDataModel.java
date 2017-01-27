
package com.nytreader.alsk.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchedNewsDataModel {

    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(final Response response) {
        this.response = response;
    }

    public static class Response {
        @SerializedName("docs")
        private List<Doc> docs = null;
        @SerializedName("meta")
        private Meta meta;

        public List<Doc> getDocs() {
            return docs;
        }

        public void setDocs(final List<Doc> docs) {
            this.docs = docs;
        }
    }

    public static class Multimedia {
        @SerializedName("url")
        private String url;
        @SerializedName("caption")
        private String caption;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(final String caption) {
            this.caption = caption;
        }
    }

    public static class Meta {
        @SerializedName("hits")
        private Integer hits;
        @SerializedName("offset")
        private Integer offset;

        public Integer getHits() {
            return hits;
        }

        public void setHits(final Integer hits) {
            this.hits = hits;
        }
    }

    public static class Headline {
        @SerializedName("name")
        private String name;
        @SerializedName("main")
        private String main;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Doc {
        @SerializedName("snippet")
        private String snippet;
        @SerializedName("headline")
        private Headline headline;
        @SerializedName("pub_date")
        private String publicationDate;
        @SerializedName("multimedia")
        private List<Multimedia> multimedia = null;

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public Headline getHeadline() {
            return headline;
        }

        public void setHeadline(Headline headline) {
            this.headline = headline;
        }

        public String getPublicationDate() {
            return publicationDate;
        }

        public void setPublicationDate(String publicationDate) {
            this.publicationDate = publicationDate;
        }

        public List<Multimedia> getMultimedia() {
            return multimedia;
        }

        public void setMultimedia(List<Multimedia> multimedia) {
            this.multimedia = multimedia;
        }
    }
}
