package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler 

import spock.lang.Unroll

@DataJpaTest
class CreateActivitySuggestionServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"
    
    def institution
    def volunteer
    
    def setup() {
        institution = institutionService.getDemoInstitution()
        volunteer = userService.getDemoVolunteer()
    }

    def "create activity suggestion"() {
        given:
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TWELVE_DAYS)

        when:
        def result = activitySuggestionService.createActivitySuggestion(volunteer.getId(), institution.getId(), activitySuggestionDto)

        then:
        result.name == ACTIVITY_NAME_1
        result.region == ACTIVITY_REGION_1
        result.participantsNumberLimit == 1
        result.description == ACTIVITY_DESCRIPTION_1
        result.startingDate == DateHandler.toISOString(IN_NINE_DAYS)
        result.endingDate == DateHandler.toISOString(IN_TWELVE_DAYS)
        result.applicationDeadline == DateHandler.toISOString(IN_EIGHT_DAYS)
        result.institution.id == institution.id
        result.volunteer.id == volunteer.id
        result.getState() == ActivitySuggestion.State.IN_REVIEW.name()
    }

    @Unroll
    def 'create activity suggestion when volunteerId or institutionId is not valid'() {
        given:
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TWELVE_DAYS)

        when:
        activitySuggestionService.createActivitySuggestion(getVolunteerId(volunteerId), getInstitutionId(institutionId), activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        volunteerId         | institutionId         || errorMessage
        null                | EXIST                 || ErrorMessage.USER_NOT_FOUND
        NO_EXIST            | EXIST                  || ErrorMessage.USER_NOT_FOUND
        EXIST               | null                  || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST               | NO_EXIST              || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    def getVolunteerId(volunteerId) {
        if (volunteerId == EXIST)
            return volunteer.getId()
        else
            return 222
        return null
    }

    def getInstitutionId(institutionId) {
        if (institutionId == EXIST)
            return institution.getId()
        else
            return 222
        return null
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

