package com.concertbuddy.concertbuddyfinder.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.javatuples.Pair;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @ElementCollection
    @JsonIgnore
    private List<Pair<UUID, Status>> usersInfo;
    @Column(nullable = false)
    private String venue;
    @Column(nullable = false)
    private String performingArtist;
    @Column(nullable = false)
    private LocalDate dateTime;
    @Column(nullable = false)
    private String genre;
    @Column(nullable = false)
    private String subGenre;

    public Concert(UUID id, String name, List<Pair<UUID, Status>> usersInfo, String venue, String performingArtist, LocalDate dateTime, String genre, String subGenre) {
        this.id = id;
        this.name = name;
        this.usersInfo = usersInfo;
        this.venue = venue;
        this.performingArtist = performingArtist;
        this.dateTime = dateTime;
        this.genre = genre;
        this.subGenre = subGenre;
    }

    public Concert() {

    }

    public Concert(String name, String venue, String performingArtist, LocalDate dateTime, String genre, String subGenre) {
        this.name = name;
        this.venue = venue;
        this.performingArtist = performingArtist;
        this.dateTime = dateTime;
        this.genre = genre;
        this.subGenre = subGenre;
    }

    public Concert(String name, List<Pair<UUID, Status>> usersInfo, String venue, String performingArtist, LocalDate dateTime, String genre, String subGenre) {
        this.name = name;
        this.usersInfo = usersInfo;
        this.venue = venue;
        this.performingArtist = performingArtist;
        this.dateTime = dateTime;
        this.genre = genre;
        this.subGenre = subGenre;
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

    public List<Pair<UUID, Status>> getUsersInfo() {
        return usersInfo;
    }

    public void setUsersInfo(List<Pair<UUID, Status>> usersInfo) {
        this.usersInfo = usersInfo;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getPerformingArtist() {
        return performingArtist;
    }

    public void setPerformingArtist(String performingArtist) {
        this.performingArtist = performingArtist;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSubGenre() {
        return subGenre;
    }

    public void setSubGenre(String subGenre) {
        this.subGenre = subGenre;
    }

    @Override
    public String toString() {
        return "Concert{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", usersInfo=" + usersInfo +
                ", venue='" + venue + '\'' +
                ", performingArtist='" + performingArtist + '\'' +
                ", dateTime=" + dateTime +
                ", genre='" + genre + '\'' +
                ", subGenre='" + subGenre +
                '}';
    }
}
