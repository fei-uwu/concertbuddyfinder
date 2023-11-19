package com.concertbuddy.concertbuddyfinder.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String artist;
    @ElementCollection
    private List<String> genre;

    public Song() {
    }

    public Song(UUID id, String name, String artist, List<String> genre) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.genre = genre;
    }

    public Song(String name, String artist, List<String> genre) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", genre=" + genre +
                '}';
    }
}
