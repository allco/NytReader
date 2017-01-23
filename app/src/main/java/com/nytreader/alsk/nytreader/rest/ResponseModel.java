
package com.nytreader.alsk.nytreader.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {

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

    public static class Multimedium {
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

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    public static class Doc {
        @SerializedName("abstract")
        private String _abstract;
        @SerializedName("headline")
        private Headline headline;
        @SerializedName("pub_date")
        private String pub_date;
        @SerializedName("multimedia")
        private List<Multimedium> multimedia = null;

        public String get_abstract() {
            return _abstract;
        }

        public void set_abstract(final String _abstract) {
            this._abstract = _abstract;
        }

        public Headline getHeadline() {
            return headline;
        }

        public void setHeadline(final Headline headline) {
            this.headline = headline;
        }

        public String getPub_date() {
            return pub_date;
        }

        public void setPub_date(final String pub_date) {
            this.pub_date = pub_date;
        }

        public List<Multimedium> getMultimedia() {
            return multimedia;
        }

        public void setMultimedia(final List<Multimedium> multimedia) {
            this.multimedia = multimedia;
        }
    }
}
