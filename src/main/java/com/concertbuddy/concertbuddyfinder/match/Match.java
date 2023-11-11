package com.concertbuddy.concertbuddyfinder.match;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="`Match`")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private UUID concertId;
    @ElementCollection
    private List<UUID> matchedUserId;

    public Match() {
    }

    public Match(UUID id, UUID userId, UUID concertId, List<UUID> matchedUserId) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.matchedUserId = matchedUserId;
    }

    public Match(UUID userId, UUID concertId, List<UUID> matchedUserId) {
        this.userId = userId;
        this.concertId = concertId;
        this.matchedUserId = matchedUserId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getConcertId() {
        return concertId;
    }

    public void setConcertId(UUID concertId) {
        this.concertId = concertId;
    }

    public List<UUID> getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(List<UUID> matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", userId=" + userId +
                ", concertId=" + concertId +
                ", matchedUserId=" + matchedUserId +
                '}';
    }
}
