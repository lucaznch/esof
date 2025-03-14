package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException


@DataJpaTest
class CreateActivitySuggestionMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    ActivitySuggestion otherActivitySuggestion = Mock()
    def activitySuggestionDto

    def setup() {
        given: "activity suggestion info"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.participantsNumberLimit = 2
        activitySuggestionDto.name = ACTIVITY_NAME_1
        activitySuggestionDto.description = ACTIVITY_DESCRIPTION_1
        activitySuggestionDto.region = ACTIVITY_REGION_1
        activitySuggestionDto.startingDate = DateHandler.toISOString(IN_NINE_DAYS)
        activitySuggestionDto.endingDate = DateHandler.toISOString(IN_TWELVE_DAYS)
        activitySuggestionDto.applicationDeadline = DateHandler.toISOString(IN_EIGHT_DAYS)
    }

    def "create activity suggestion successfully"() {
        when: "create activity suggestion"
        def result = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then: "check result"
        result.getParticipantsNumberLimit() == 2
        result.getName() == ACTIVITY_NAME_1
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getStartingDate() == IN_NINE_DAYS
        result.getEndingDate() == IN_TWELVE_DAYS
        result.getApplicationDeadline() == IN_EIGHT_DAYS
        result.getInstitution() == institution
        result.getVolunteer() == volunteer
        and: "invocations"
        1 * institution.addActivitySuggestion(_)
        1 * volunteer.addActivitySuggestion(_)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

