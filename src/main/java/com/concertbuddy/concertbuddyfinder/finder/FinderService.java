package com.concertbuddy.concertbuddyfinder.finder;

import com.concertbuddy.concertbuddyfinder.match.MatchRepository;
// import com.concertbuddy.concertbuddyfinder.models.Song;
import com.concertbuddy.concertbuddyfinder.models.SongsResponse;
import com.concertbuddy.concertbuddyfinder.models.SongsResponse.Song;
import com.concertbuddy.concertbuddyfinder.models.User;
import com.concertbuddy.concertbuddyfinder.models.UserSimilarity;

import com.concertbuddy.concertbuddyfinder.match.Match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FinderService {

    private final MatchRepository matchRepository;
    private static final Integer MAX_AGE = 100;
    private static final Integer NUMBER_OF_MATCHES = 3;

    @Autowired
    public FinderService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    // TODO: When calculate similarity, only use users that are INTERESTED + ATTENDING the concert
    public Match FindMatch(UUID userId, UUID concertId) {
        RestTemplate restTemplate = new RestTemplate();
        // Get user info
        String getUserUrl = "http://ec2-18-188-69-200.us-east-2.compute.amazonaws.com:8012/api/v1/users/" + userId;
        User user1 = restTemplate.getForObject(getUserUrl, User.class);

        String getSongUrl = String.format("http://ec2-18-188-69-200.us-east-2.compute.amazonaws.com:8012/api/v1/users/%s/songs", userId.toString());
        SongsResponse user1Songs = restTemplate.getForObject(getSongUrl, SongsResponse.class);
        user1.setSongs(user1Songs.get_embedded().getSongList());

        // Get all users info
        String getUsersUrl = "http://ec2-18-188-69-200.us-east-2.compute.amazonaws.com:8012/api/v1/users";
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(getUsersUrl, User[].class);
        List<User> users = Arrays.asList(responseEntity.getBody()).stream()
                .filter(u -> (u.getId() != userId) )
                .collect(Collectors.toList());
        System.out.println("All users info");
        for (User u : users) {
            String getUserSongUrl = String.format("http://ec2-18-188-69-200.us-east-2.compute.amazonaws.com:8012/api/v1/users/%s/songs", u.getId().toString());
            SongsResponse userSongs = restTemplate.getForObject(getUserSongUrl, SongsResponse.class);
            u.setSongs(userSongs.get_embedded().getSongList());
            System.out.println(u.toString());
        }

        // Find top 3 most similar users
        List<User> mostSimilarUsers = findMostSimilarUsers(user1, users, 0.3);

        System.out.println("Top 3 most similar users to user1:");
        for (User user : mostSimilarUsers) {
            System.out.println(user);
        }
        List<UUID> matchesIds = mostSimilarUsers.stream().map(u -> u.getId()).collect(Collectors.toList());

        Match matches = new Match(userId, concertId, matchesIds);
        return matchRepository.save(matches);
    }

    public static List<User> findMostSimilarUsers(User user1, List<User> user2List, double ageWeight) {
        List<UserSimilarity> userSimilarities = new ArrayList<>();

        for (User user2 : user2List) {
            double similarity = calculateOverallSimilarity(user1, user2, ageWeight);
            System.out.println("Similarity for user " + user2.getName() + ": " + similarity);
            userSimilarities.add(new UserSimilarity(user2, similarity));
        }

        // Sort users by similarity in descending order
        Collections.sort(userSimilarities, Comparator.reverseOrder());

        // Get the top 3 most similar users
        List<User> mostSimilarUsers = new ArrayList<>();
        int topUsersCount = Math.min(userSimilarities.size(), NUMBER_OF_MATCHES);

        for (int i = 0; i < topUsersCount; i++) {
            mostSimilarUsers.add(userSimilarities.get(i).getUser());
        }

        return mostSimilarUsers;
    }

    public static double calculateOverallSimilarity(User user1, User user2, double ageWeight) {
        double ageSimilarity = calculateGaussianAgeSimilarity(user1.getAge(), user2.getAge(), MAX_AGE);
        System.out.println("Age Similarity: " + ageSimilarity);
        double songSimilarity = calculateSongSimilarity(user1.getSongs(), user2.getSongs());
        System.out.println("Song Similarity: " + songSimilarity);

        // Combine similarities with a weighted sum
        double overallSimilarity = (ageWeight * ageSimilarity) + ((1 - ageWeight) * songSimilarity);

        return overallSimilarity;
    }

    public static double calculateGaussianAgeSimilarity(double user1Age, double user2Age, double sigma) {
        // Calculate the Gaussian function to measure age similarity
        double exponent = -Math.pow(user1Age - user2Age, 2) / (2 * Math.pow(sigma, 2));
        return Math.exp(exponent);
    }

    public static double calculateSongSimilarity(List<Song> user1Songs, List<Song> user2Songs) {
        // Calculate Jaccard similarity for song genres
        Set<String> allGenres1 = new HashSet<>();
        Set<String> allGenres2 = new HashSet<>();

        for (Song song : user1Songs) {
            Set<String> genres = new HashSet<String>(song.getGenre());
            allGenres1.addAll(genres);
        }
        for (Song song : user2Songs) {
            Set<String> genres = new HashSet<String>(song.getGenre());
            allGenres2.addAll(genres);
        }
        double genreSimilarity = calculateSongInfoSimilarity(allGenres1, allGenres2);
        System.out.println("Genre similarity: " + genreSimilarity);

        // Calculate Jaccard similarity for song names
        Set<String> songNames1 = new HashSet<>();
        Set<String> songNames2 = new HashSet<>();

        for (Song song : user1Songs) {
            songNames1.add(song.getName());
        }
        for (Song song : user2Songs) {
            songNames2.add(song.getName());
        }

        double nameSimilarity = calculateSongNameSimilarity(songNames1, songNames2);
        System.out.println("Song Name Similarity: " + nameSimilarity);

        // Calculate Jaccard similarity for song artists
        Set<String> artists1 = new HashSet<>();
        Set<String> artists2 = new HashSet<>();

        for (Song song : user1Songs) {
            artists1.add(song.getArtist());
        }

        for (Song song : user2Songs) {
            artists2.add(song.getArtist());
        }
        double artistSimilarity = calculateSongInfoSimilarity(artists1, artists2);
        System.out.println("Artist similarity: " + artistSimilarity);

        // Combine genre, name, and artist similarities with equal weight
        return (genreSimilarity + nameSimilarity + artistSimilarity) / 3.0;
    }

    public static double calculateSongInfoSimilarity(Set<String> info1, Set<String> info2) {
        // Calculate Jaccard similarity for song names
        Set<String> intersection = new HashSet<>(info1);
        intersection.retainAll(info2);

        Set<String> union = new HashSet<>(info1);
        union.addAll(info2);

        double infoSimilarity = (double) intersection.size() / union.size();

        return infoSimilarity;
    }

    public static double calculateSongNameSimilarity(Set<String> names1, Set<String> names2) {
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

    public static double calculateJaroWinklerSimilarity(String str1, String str2) {
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

    /******** DUMMY FUNCTIONS TO BE REMOVED AFTER INTEGRATING WITH OTHER MICROSERVICES ********/
    // public static User getUserById(UUID userId) {
    //     User u = new User(
    //             userId,
    //             "test-user",
    //             LocalDate.now(),
    //             "zh2603@columbia.edu",
    //             "test-password",
    //             null,
    //             getSongs());
    //     u.setAge(21);
    //     return u;
    // }

    public static User[] getAllUsers() {
        List<Song> songsOne = Arrays.asList(
                new Song(
                        "Matsuri",
                        "Fujii Kaze",
                        Arrays.asList("J-Pop")),
                new Song(
                        "From The Start",
                        "Laufey",
                        Arrays.asList("Jazz")),
                new Song(
                        "Subtitle",
                        "Official Hige Dandism",
                        Arrays.asList("J-Pop")));
        List<Song> songsTwo = Arrays.asList(
                new Song(
                        "Playing God",
                        "Polyphia",
                        Arrays.asList("Rock")),
                new Song(
                        "G.O.A.T",
                        "Polyphia",
                        Arrays.asList("Rock")),
                new Song(
                        "Call Me Little Sunshine",
                        "Ghost",
                        Arrays.asList("Metal")));
        List<Song> songsThree = Arrays.asList(
                new Song(
                        "SPECIALZ",
                        "King Gnu",
                        Arrays.asList("J-Pop", "Rock")),
                new Song(
                        "Falling Behind",
                        "Laufey",
                        Arrays.asList("Jazz")),
                new Song(
                        "Subtitle",
                        "Official Hige Dandism",
                        Arrays.asList("J-Pop")));
        List<Song> songsFour = Arrays.asList(
                new Song(
                        "Kick Back",
                        "Kenshi Yonezu",
                        Arrays.asList("J-Pop", "Rock")),
                new Song(
                        "Lemon",
                        "Kenshi Yonezu",
                        Arrays.asList("J-Pop", "Indie", "Rock")),
                new Song(
                        "Subtitle",
                        "Official Hige Dandism",
                        Arrays.asList("J-Pop")));

        User one = new User(
                UUID.randomUUID(),
                "test-user1",
                LocalDate.now(),
                "one@gmail.com",
                "test-password",
                null,
                songsOne);
        one.setAge(22);
        User two = new User(
                UUID.randomUUID(),
                "test-user2",
                LocalDate.now(),
                "two@gmail.com",
                "test-password",
                null,
                songsTwo);
        two.setAge(20);
        User three = new User(
                UUID.randomUUID(),
                "test-user3",
                LocalDate.now(),
                "three@gmail.com",
                "test-password",
                null,
                songsThree);
        three.setAge(59);
        User four = new User(
                UUID.randomUUID(),
                "test-user4",
                LocalDate.now(),
                "four@gmail.com",
                "test-password",
                null,
                songsFour);
        four.setAge(33);
        User[] users = { one, two, three, four };
        return users;
    }

    public static List<Song> getSongs() {
        return Arrays.asList(
            new Song(
                    "Apoptosis",
                    "Official Hige Dandism",
                    Arrays.asList("Rock", "J-Pop")),
            new Song(
                    "Japanese Denim",
                    "Daniel Caesar",
                    Arrays.asList("R&B")),
            new Song(
                    "Dance Macabre",
                    "Ghost",
                    Arrays.asList("Disco")));
    }
}
