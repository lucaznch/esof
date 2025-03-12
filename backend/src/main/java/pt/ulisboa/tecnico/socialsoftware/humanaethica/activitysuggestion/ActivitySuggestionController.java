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
    

    @GetMapping()
    public List<ActivitySuggestionDto> getActivitySuggestions(Principal principal) {
        return activitySuggestionService.getActivitySuggestions();
    }

    // why not @Valid ???
    @GetMapping("/{id}")
    public List<ActivitySuggestionDto> getActivitySuggestions(@PathVariable Integer institutionId) {
        return activitySuggestionService.getActivitySuggestionsByInstitution(institutionId);
    }


/*

    para logica segunda story!

    @PutMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public ActivityDto updateActivity(@PathVariable int activityId, @Valid @RequestBody ActivityDto activityDto){
        return activityService.updateActivity(activityId, activityDto);
    }

*/


    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public ActivitySuggestionDto createActivitySuggestion(Principal principal, @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto){
        
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        int institutionId = activitySuggestionDto.getInstitution().getId();

        return activitySuggestionService.createActivitySuggestion(userId, institutionId, activitySuggestionDto);
    }


    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER'))")
    public List<ActivitySuggestionDto> getActivitySuggestionsToMember(@PathVariable Integer memberId) {
        return activitySuggestionService.getActivitySuggestions();
    }


    // pipeline tests?
   
    // invariantes estranhas  -> CONTROLLER!!  -> hasPermission

    // verificar memeber sotyry

    // jacoco -> tirar screenshot index.html
}
