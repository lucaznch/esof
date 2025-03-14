package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



@RestController
@RequestMapping("/activitysuggestions")
public class ActivitySuggestionController {
    @Autowired
    private ActivitySuggestionService activitySuggestionService;

    private static final Logger logger = LoggerFactory.getLogger(ActivitySuggestionController.class);


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /*      access conditions                                                                              */
    /*    - only a volunteer can suggest an activity                                                       */
    /*    - only a member of an institution can get the list of suggested activities for that institution  */
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    @GetMapping("/{institutionId}")
    @PreAuthorize("(hasRole('ROLE_MEMBER'))")
    public List<ActivitySuggestionDto> getActivitySuggestions(@PathVariable Integer institutionId) {
        return activitySuggestionService.getActivitySuggestionsByInstitution(institutionId);
    }

    @PostMapping("/{institutionId}")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public ActivitySuggestionDto createActivitySuggestion(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        
        return activitySuggestionService.createActivitySuggestion(userId, institutionId, activitySuggestionDto);
    }
}

