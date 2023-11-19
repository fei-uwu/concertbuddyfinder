package com.concertbuddy.concertbuddyfinder.models;

public class UserSimilarity implements Comparable<UserSimilarity> {
    private User user;
    private double similarity;

    public UserSimilarity(User user, double similarity) {
        this.user = user;
        this.similarity = similarity;
    }

    public User getUser() {
        return user;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public int compareTo(UserSimilarity other) {
        // Compare by similarity (descending order)
        return Double.compare(this.similarity, other.similarity);
    }
}