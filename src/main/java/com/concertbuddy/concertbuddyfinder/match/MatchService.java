package com.concertbuddy.concertbuddyfinder.match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(UUID matchId) {
        Optional<Match> optionalMatchById = matchRepository.findById(matchId);
        if (optionalMatchById.isEmpty()) {
            throw new IllegalStateException(
                    "Match with id " + matchId + " does not exist"
            );
        }
        return optionalMatchById.get();
    }

    public void addNewMatch(Match match) {
        matchRepository.save(match);
    }

    public void deleteMatch(UUID matchId) {
        boolean exists = matchRepository.existsById(matchId);
        if (!exists) {
            throw new IllegalStateException(
                    "Match with id " + matchId + " does not exist"
            );
        }
        matchRepository.deleteById(matchId);
    }

}
