package com.concertbuddy.concertbuddyfinder.models;

import java.util.List;

public class SongsResponse {
    private Embedded _embedded;

    public Embedded get_embedded() {
        return _embedded;
    }

    public void set_embedded(Embedded _embedded) {
        this._embedded = _embedded;
    }

    public static class Embedded {
        private List<Song> songList;

        public List<Song> getSongList() {
            return songList;
        }

        public void setSongList(List<Song> songList) {
            this.songList = songList;
        }
    }

    public static class Song {
        private String id;
        private String name;
        private String artist;
        private List<String> genre;
        private Links _links;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public List<String> getGenre() {
            return genre;
        }

        public void setGenre(List<String> genre) {
            this.genre = genre;
        }

        public Links get_links() {
            return _links;
        }

        public void set_links(Links _links) {
            this._links = _links;
        }
    }

    public static class Links {
        private Self self;

        public Self getSelf() {
            return self;
        }

        public void setSelf(Self self) {
            this.self = self;
        }
    }

    public static class Self {
        private String href;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }
}