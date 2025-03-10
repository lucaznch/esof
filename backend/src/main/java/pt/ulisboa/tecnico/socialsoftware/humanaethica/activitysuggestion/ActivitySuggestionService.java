package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.repository.ActivitySuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.List;
import java.util.Comparator;


@Service
public class ActivitySuggestionService {
    @Autowired
    ActivitySuggestionRepository suggestionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstitutionRepository institutionRepository;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getSuggestions() {
        return suggestionRepository.findAll().stream()
                .map(suggestion -> new ActivitySuggestionDto(suggestion, true, true))
                .sorted(Comparator.comparing(ActivitySuggestionDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto registerSuggestion(Integer userId, Integer institutionId, ActivitySuggestionDto suggestionDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);

        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));

        ActivitySuggestion suggestion = new ActivitySuggestion(suggestionDto, institution, volunteer);

        suggestionRepository.save(suggestion);

        return new ActivitySuggestionDto();
    }
}