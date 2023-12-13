package com.concertbuddy.concertbuddyfinder.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/finder/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public List<Match> getMatches() {
        return matchService.getMatches();
    }

    @GetMapping(path="{matchId}")
    public Match getMatchById(@PathVariable("matchId") UUID matchId) {
        return matchService.getMatchById(matchId);
    }

    @PostMapping
    public void registerNewMatch(@RequestBody Match match) {
        matchService.addNewMatch(match);
    }

    @PutMapping
    public void updateMatch(@RequestBody Match match) {
        matchService.addNewMatch(match);
    }

    @DeleteMapping(path="{matchId}")
    public void deleteMatch(@PathVariable("matchId") UUID matchId) {
        matchService.deleteMatch(matchId);
    }
}
