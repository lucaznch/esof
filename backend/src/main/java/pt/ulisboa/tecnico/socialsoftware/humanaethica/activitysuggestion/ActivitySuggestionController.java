package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activitySuggestions")
public class ActivitySuggestionController {
    @Autowired
    private ActivitySuggestionService activitySuggestionService;

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public List<ActivitySuggestionDto> getActivitySuggestions(@PathVariable Integer institutionId) {
        return this.activitySuggestionService.getActivitySuggestionsByInstitution(institutionId);
    }

    @GetMapping("/volunteer/{volunteerId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public List<ActivitySuggestionDto> getActivitySuggestionsByVolunteer(@PathVariable Integer volunteerId) {
        return this.activitySuggestionService.getActivitySuggestionsByVolunteer(volunteerId);
    }

    @PostMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto createActivitySuggestion(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.createActivitySuggestion(userId, institutionId, activitySuggestionDto);
    }

    @PutMapping("/approve/{activitySuggestionId}/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public ActivitySuggestionDto approveActivitySuggestion(@PathVariable Integer activitySuggestionId, @PathVariable Integer institutionId) {
        return activitySuggestionService.approveActivitySuggestion(activitySuggestionId, institutionId);
    }

    @PutMapping("/reject/{activitySuggestionId}/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public ActivitySuggestionDto rejectActivitySuggestion(@PathVariable Integer activitySuggestionId, @PathVariable Integer institutionId) {
        return activitySuggestionService.rejectActivitySuggestion(activitySuggestionId, institutionId);
    }
}