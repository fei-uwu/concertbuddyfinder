package com.concertbuddy.concertbuddyfinder.finder;

import com.concertbuddy.concertbuddyfinder.match.MatchRepository;
import com.concertbuddy.concertbuddyfinder.models.ConcertUser;
import com.concertbuddy.concertbuddyfinder.models.SongsResponse;
import com.concertbuddy.concertbuddyfinder.models.User;
import com.concertbuddy.concertbuddyfinder.models.UserSimilarity;

import com.concertbuddy.concertbuddyfinder.match.Match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
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
    private final static RestTemplate restTemplate = new RestTemplate();
    private static final Integer NUMBER_OF_MATCHES = 3;
    private static final String USER_MICROSERVICE_URL = "http://ec2-18-224-179-229.us-east-2.compute.amazonaws.com:8012";
    private static final String CONCERT_MICROSERVICE_URL = "http://concertbuddyconcert.uc.r.appspot.com";

    @Autowired
    public FinderService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match FindMatch(UUID userId, UUID concertId) {
        // Get current user info
        String getUserUrl = USER_MICROSERVICE_URL + "/api/v1/users/" + userId;
        User currentUser = restTemplate.getForObject(getUserUrl, User.class);

        // Get all songs for current user
        getUserSongs(currentUser);
        System.out.println("[" + LocalDateTime.now() + "] DEBUG " + 
                "Current user: " + currentUser);
        
        // Get all users INTERESTED/ATTENDING the concert
        List<User> concertUsersWithSongs = getConcertUsers(userId, concertId);
        System.out.println("[" + LocalDateTime.now() + "] DEBUG " +  
                "All user info for concert " + concertId);
        for (User user : concertUsersWithSongs) {
            System.out.println(user);
        }

        // Find top 3 most similar users
        List<User> mostSimilarUsers = findMostSimilarUsers(currentUser, concertUsersWithSongs, 0.3);

        System.out.println("[" + LocalDateTime.now() + "] DEBUG " +  
                "Top 3 most similar users to " + currentUser.getId());
        for (User user : mostSimilarUsers) {
            System.out.println(user);
        }
        List<UUID> matchesIds = mostSimilarUsers.stream().map(u -> u.getId()).collect(Collectors.toList());

        Match matches = new Match(userId, concertId, matchesIds);
        return matchRepository.save(matches);
    }

    private List<User> getAllUsers(UUID currentUserId) {
        String getUsersUrl = USER_MICROSERVICE_URL + "/api/v1/users";
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(getUsersUrl, User[].class);

        List<User> users = Arrays.asList(responseEntity.getBody()).stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .collect(Collectors.toList());
        List<User> usersWithSongs = updateUserSongs(users);
        return usersWithSongs;
    }

    private List<User> getConcertUsers(UUID currentUserId, UUID concertId) {
        String getConcertUsersUrl = CONCERT_MICROSERVICE_URL + "/api/v1/concerts/" + concertId + "/usersInfo";
        List<ConcertUser> concertUserIds = new ArrayList<ConcertUser>();
        try {
            ResponseEntity<ConcertUser[]> concertUsersResponse = restTemplate.getForEntity(getConcertUsersUrl, ConcertUser[].class);
            concertUserIds = Arrays.asList(concertUsersResponse.getBody()).stream()
                .filter(u -> !u.getValue0().equals(currentUserId))
                .collect(Collectors.toList());
        } catch (NullPointerException e) {
            System.out.println("[" + LocalDateTime.now() + "] ERROR " +
                "Error getting user infos for concert " + concertId);
            throw e;
        }
        if (concertUserIds.isEmpty()) {
            System.out.println("[" + LocalDateTime.now() + "] DEBUG " +
                "No users going to concert " + concertId);
            return getAllUsers(currentUserId);
        }

        List<User> concertUsers = new ArrayList<User>();
        for (ConcertUser concertUser : concertUserIds) {
            String getUserUrl = USER_MICROSERVICE_URL + "/api/v1/users/" + concertUser.getValue0();
            User user = restTemplate.getForObject(getUserUrl, User.class);
            concertUsers.add(user);
        }

        List<User> concertUsersWithSongs = updateUserSongs(concertUsers);
        return concertUsersWithSongs;
    }

    private static List<User> updateUserSongs(List<User> users) {
        List<User> usersWithSongs = new ArrayList<User>();
        for (User u : users) {
            User userWithSongs = getUserSongs(u);
            if (userWithSongs != null) {
                usersWithSongs.add(userWithSongs);    
            }
        }
        return usersWithSongs;
    }

    private static User getUserSongs(User u) {
        String getUserSongUrl = USER_MICROSERVICE_URL + "/api/v1/users/"+ u.getId() + "/songs";
        try {
            SongsResponse userSongs = restTemplate.getForObject(getUserSongUrl, SongsResponse.class);
            u.setSongs(userSongs.get_embedded().getSongList());
        } catch (NullPointerException e) {
            System.out.println("[" + LocalDateTime.now() + "] DEBUG " +
                "User " + u.getId() + " does not have any songs.");
            return null;
        }
        return u;
    }

    private static List<User> findMostSimilarUsers(User currentUser, List<User> user2List, double ageWeight) {
        List<UserSimilarity> userSimilarities = new ArrayList<>();

        for (User user2 : user2List) {
            double similarity = FinderAlgorithm.calculateOverallSimilarity(currentUser, user2, ageWeight);
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
}
