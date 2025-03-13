package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import spock.lang.Unroll

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

    // Domain tests assume that institution and volunteer contain valid values

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

    @Unroll
    def "create activity and violate description length invariant"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        institution.getActivitySuggestions() >> [otherActivitySuggestion]
        and: "an activity suggestion dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setParticipantsNumberLimit(2)
        activitySuggestionDto.setName(ACTIVITY_NAME_1)
        activitySuggestionDto.setDescription(description)
        activitySuggestionDto.setRegion(ACTIVITY_REGION_1)
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_NINE_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_TWELVE_DAYS))
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(IN_EIGHT_DAYS))

        when:
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_LENGTH_INVALID

        where:
        description << ["a", "ab", "abc", "abcd", "abcde", "abcdef", "abcdefg", "abcdefgh", "abcdefghi"]
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

