package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



// what is Principal?
@RestController
@RequestMapping("/suggestions")
public class ActivitySuggestionController {

    @Autowired
    private ActivitySuggestionService suggestionService;

    private static final Logger logger = LoggerFactory.getLogger(ActivitySuggestionController.class);
    

    @GetMapping()
    public List<ActivitySuggestionDto> getSuggestions(Principal principal) {
        return suggestionService.getSuggestions();
    }

    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public ActivitySuggestionDto registerSuggestion(Principal principal, @Valid @RequestBody ActivitySuggestionDto suggestionDto){
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        int institutionId = 1; // how to get institutionId?

        return suggestionService.registerSuggestion(userId, institutionId, suggestionDto);
    }
}
