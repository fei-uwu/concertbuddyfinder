package com.concertbuddy.concertbuddyfinder.finder;

import com.concertbuddy.concertbuddyfinder.match.MatchController;
import com.concertbuddy.concertbuddyfinder.snslambda.SendEmailLambda;
import com.concertbuddy.concertbuddyfinder.snslambda.SendEmailSns;
import com.concertbuddy.concertbuddyfinder.match.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/finder")
@CrossOrigin(origins = "*")
public class FinderController {
    private final FinderService finderService;

    @Autowired
    public FinderController(FinderService finderService) {
        this.finderService = finderService;
    }

    @PostMapping("{userId}/{concertId}")
    public ResponseEntity<EntityModel<Match>> findMatch(@PathVariable("userId") UUID userId, @PathVariable("concertId") UUID concertId) {
        Match match = finderService.FindMatch(userId, concertId);

        // Use SNS and Lambda to notify users matches are ready
        // Publish user email to SNS for Lambda to send email
        SendEmailSns.sendEmail(getUserEmail(userId));

        EntityModel<Match> matchWithLinks = EntityModel.of(
                        match,
                        linkTo(methodOn(MatchController.class).getMatchById(match.getId())).withSelfRel());
        return ResponseEntity.ok(matchWithLinks);
    }

    private String getUserEmail(UUID userId) {
        return "zh2603@columbia.edu";
    }
}
