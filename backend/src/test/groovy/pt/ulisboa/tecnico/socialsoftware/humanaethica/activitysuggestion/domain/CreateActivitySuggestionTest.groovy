package pt.ulisboa.tecnico.socialsoftware.humanaethica.suggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.suggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.SUGGESTION_NAME_UNIQUE_FOR_VOLUNTEER


@DataJpaTest
class SuggestActivityTest extends SpockTest{
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    ActivitySuggestion otherSuggestion = Mock()
    def activitySuggestionDto

    def setup(){
        given: "activity suggestion info"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.name = SUGGESTION_NAME_1
        activitySuggestionDto.description = SUGGESTION_DESCRIPTION_1
        activitySuggestionDto.participantsNumberLimit = 2
        activitySuggestionDto.region = ACTIVITY_REGION_1
        activitySuggestionDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activitySuggestionDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activitySuggestionDto.applicationDeadline = DateHandler.toISOString(IN_SEVEN_DAYS)
    }

    @Unroll
    def "create suggestion violate description length"(){
        given:
        activitySuggestionDto.description = SUGGESTION_DESCRIPTION_WRONG

        when:
        new ActivitySuggestion(activitySuggestionDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.SUGGESTION_DESCRIPTION_LENGTH_INVALID
    }

    @Unroll
    def "create suggestion violate unique name invariant"(){
        given:
        otherSuggestion.getName() >> SUGGESTION_NAME_2
        volunteer.getActivitySuggestions() >> [otherSuggestion]
        and: "an activity suggestion dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setName(SUGGESTION_NAME_2)
        activitySuggestionDto.setDescription(SUGGESTION_DESCRIPTION_1)
        activitySuggestionDto.setRegion(SUGGESTION_REGION_1)
        activitySuggestionDto.setCreationDate()
        activitySuggestionDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activitySuggestionDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activitySuggestionDto.applicationDeadline = DateHandler.toISOString(IN_SEVEN_DAYS)

        when:
        new ActivitySuggestion(activitySuggestionDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == Error.Message(SUGGESTION_NAME_UNIQUE_FOR_VOLUNTEER)
    }




}