package com.concertbuddy.concertbuddyfinder.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class ConcertUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID value0;
    @Column(nullable = false)
    private Integer size;
    @Column(nullable = false)
    private Status value1;
    
    public ConcertUser() {
    }

    public ConcertUser(Integer size, UUID value0, Status value1) {
        this.size = size;
        this.value0 = value0;
        this.value1 = value1;
    }

    public Integer getSize() {
        return size;
    }

    public UUID getValue0() {
        return value0;
    }

    public Status getValue1() {
        return value1;
    }

    @Override
    public String toString() {
        return "ConcertUser{" +
                "size='" + size + '\'' +
                ", id='" + value0 + '\'' +
                ", status='" + value1 + '\'' +
                '}';
    }
}