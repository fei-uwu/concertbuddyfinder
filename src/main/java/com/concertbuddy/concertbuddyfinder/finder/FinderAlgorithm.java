package com.concertbuddy.concertbuddyfinder.finder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.concertbuddy.concertbuddyfinder.models.SongsResponse.Song;
import com.concertbuddy.concertbuddyfinder.models.User;

public class FinderAlgorithm {
    private static final Integer MAX_AGE = 100;

    public static double calculateOverallSimilarity(User currentUser, User user2, double ageWeight) {
        double ageSimilarity = calculateGaussianAgeSimilarity(currentUser.getAge(), user2.getAge(), MAX_AGE);
        double songSimilarity = calculateSongSimilarity(currentUser.getSongs(), user2.getSongs());

        // Combine similarities with a weighted sum
        double overallSimilarity = (ageWeight * ageSimilarity) + ((1 - ageWeight) * songSimilarity);

        return overallSimilarity;
    }

    private static double calculateGaussianAgeSimilarity(double currentUserAge, double user2Age, double sigma) {
        // Calculate the Gaussian function to measure age similarity
        double exponent = -Math.pow(currentUserAge - user2Age, 2) / (2 * Math.pow(sigma, 2));
        return Math.exp(exponent);
    }

    private static double calculateSongSimilarity(List<Song> currentUserSongs, List<Song> user2Songs) {
        // Calculate Jaccard similarity for song genres
        Set<String> allGenres1 = new HashSet<>();
        Set<String> allGenres2 = new HashSet<>();

        for (Song song : currentUserSongs) {
            Set<String> genres = new HashSet<String>(song.getGenre());
            allGenres1.addAll(genres);
        }
        for (Song song : user2Songs) {
            Set<String> genres = new HashSet<String>(song.getGenre());
            allGenres2.addAll(genres);
        }
        double genreSimilarity = calculateSongInfoSimilarity(allGenres1, allGenres2);

        // Calculate Jaccard similarity for song names
        Set<String> songNames1 = new HashSet<>();
        Set<String> songNames2 = new HashSet<>();

        for (Song song : currentUserSongs) {
            songNames1.add(song.getName());
        }
        for (Song song : user2Songs) {
            songNames2.add(song.getName());
        }

        double nameSimilarity = calculateSongNameSimilarity(songNames1, songNames2);

        // Calculate Jaccard similarity for song artists
        Set<String> artists1 = new HashSet<>();
        Set<String> artists2 = new HashSet<>();

        for (Song song : currentUserSongs) {
            artists1.add(song.getArtist());
        }

        for (Song song : user2Songs) {
            artists2.add(song.getArtist());
        }
        double artistSimilarity = calculateSongInfoSimilarity(artists1, artists2);

        // Combine genre, name, and artist similarities with equal weight
        return (genreSimilarity + nameSimilarity + artistSimilarity) / 3.0;
    }

    private static double calculateSongInfoSimilarity(Set<String> info1, Set<String> info2) {
        // Calculate Jaccard similarity for song names
        Set<String> intersection = new HashSet<>(info1);
        intersection.retainAll(info2);

        Set<String> union = new HashSet<>(info1);
        union.addAll(info2);

        double infoSimilarity = (double) intersection.size() / union.size();

        return infoSimilarity;
    }

    private static double calculateSongNameSimilarity(Set<String> names1, Set<String> names2) {
        double totalSimilarity = 0.0;
        int pairCount = 0;

        // Calculate Jaro-Winkler similarity for each pair of song names
        for (String name1 : names1) {
            for (String name2 : names2) {
                double similarity = calculateJaroWinklerSimilarity(name1, name2);
                totalSimilarity += similarity;
                pairCount++;
            }
        }

        // Calculate the average similarity
        double averageSimilarity = pairCount > 0 ? totalSimilarity / pairCount : 0.0;

        return averageSimilarity;
    }

    private static double calculateJaroWinklerSimilarity(String str1, String str2) {
        // Jaro-Winkler similarity calculation
        int prefixLength = 0;

        // Find the length of the common prefix
        for (int i = 0; i < Math.min(str1.length(), str2.length()); i++) {
            if (str1.charAt(i) == str2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        // Jaro similarity
        double jaroSimilarity = JaroSimilarity(str1, str2);

        // Winkler modification
        double winklerSimilarity = jaroSimilarity + (0.1 * prefixLength * (1 - jaroSimilarity));

        return winklerSimilarity;
    }

    private static double JaroSimilarity(String str1, String str2) {
        int matchWindow = Math.max(0, Math.max(str1.length(), str2.length()) / 2 - 1);

        // Count matches
        int matches = 0;
        int transpositions = 0;

        boolean[] str1Matches = new boolean[str1.length()];
        boolean[] str2Matches = new boolean[str2.length()];

        for (int i = 0; i < str1.length(); i++) {
            int start = Math.max(0, i - matchWindow);
            int end = Math.min(i + matchWindow + 1, str2.length());

            for (int j = start; j < end; j++) {
                if (!str2Matches[j] && str1.charAt(i) == str2.charAt(j)) {
                    str1Matches[i] = true;
                    str2Matches[j] = true;
                    matches++;
                    break;
                }
            }
        }

        // Count transpositions
        int k = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1Matches[i]) {
                while (!str2Matches[k]) {
                    k++;
                }
                if (str1.charAt(i) != str2.charAt(k)) {
                    transpositions++;
                }
                k++;
            }
        }

        // Calculate Jaro similarity
        if (matches == 0) {
            return 0.0;
        }

        double jaroSimilarity = ((double) matches / str1.length() +
                (double) matches / str2.length() +
                ((double) matches - transpositions / 2.0) / matches) / 3.0;

        return jaroSimilarity;
    }
}
