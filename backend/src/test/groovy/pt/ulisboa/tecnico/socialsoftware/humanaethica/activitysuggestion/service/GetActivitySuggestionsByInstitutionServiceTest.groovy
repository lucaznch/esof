package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage

import spock.lang.Unroll

@DataJpaTest
class GetActivitySuggestionsByInstitutionServiceTest extends SpockTest {
    def institution

    def setup() {
        institution = institutionService.getDemoInstitution()
        def volunteer = userService.getDemoVolunteer()

        given: "activity suggestion info"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TWELVE_DAYS)

        and: "an activity suggestion"
        def activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)

        and: 'another activity suggestion'
        activitySuggestionDto.name = ACTIVITY_NAME_2
        activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)
    }

    def 'get two activity suggestions'() {
        when:
        def result = activitySuggestionService.getActivitySuggestionsByInstitution(institution.getId())

        then:
        result.size() == 2
        result.get(0).name == ACTIVITY_NAME_1
        result.get(1).name == ACTIVITY_NAME_2
    }

    @Unroll
    def 'get activity suggestions when institutionId is not valid'() {
        when:
        activitySuggestionService.getActivitySuggestionsByInstitution(institutionId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_NOT_FOUND

        where:
        institutionId << [null,-1]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

