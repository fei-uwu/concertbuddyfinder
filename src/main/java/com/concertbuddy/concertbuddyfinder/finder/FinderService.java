package com.concertbuddy.concertbuddyfinder.finder;

import com.concertbuddy.concertbuddyfinder.match.MatchRepository;
import com.concertbuddy.concertbuddyfinder.match.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import static java.util.Collections.emptyList;

@Service
public class FinderService {

    private final MatchRepository matchRepository;

    @Autowired
    public FinderService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match FindMatch(UUID userId, UUID concertId) {
        // add logic to get the Match
        Match exampleMatchWithoutMatchId = new Match(userId, concertId, emptyList());

        return matchRepository.save(exampleMatchWithoutMatchId);
    }
}
