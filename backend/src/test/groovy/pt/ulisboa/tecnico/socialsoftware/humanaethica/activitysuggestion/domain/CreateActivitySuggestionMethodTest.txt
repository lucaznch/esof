package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration


@DataJpaTest
class CreateActivitySuggestionMethodTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    ActivitySuggestion otherActivitySuggestion = Mock()
    def activitySuggestionDto

    def setup() {
        given: "activity suggestion info"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.getParticipantsNumberLimit = 2
        activitySuggestionDto.name = ACTIVITY_NAME_1
        activitySuggestionDto.description = ACTIVITY_DESCRIPTION_1
        activitySuggestionDto.region = ACTIVITY_REGION_1
        activitySuggestionDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activitySuggestionDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activitySuggestionDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
    }



    @Unroll
    def "create activity suggestion and violate invariants: nameIsRequired, descriptionIsRequired, regionIsRequired"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]

        and: "an activity suggestion dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setParticipantsNumberLimit(1)
        activitySuggestionDto.setName(name)
        activitySuggestionDto.setDescription(description)
        activitySuggestionDto.setRegion(region)
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        when:
        new ActivitySuggestion(activitySuggestionDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() = error_message

        where:
        name            | region            | description            || errorMessage
        null            | ACTIVITY_REGION_1 | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_NAME_INVALID
        " "             | ACTIVITY_REGION_1 | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_1 | null              | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | " "               | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | null                   || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | "  "                   || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
    }

    @Unroll
    def "create activity suggestion and violate description length invariant: must provide a description with at least 10 characters"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]

        and: "an activity suggestion dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setParticipantsNumberLimit(1)
        activitySuggestionDto.setName(ACTIVITY_NAME_1)
        activitySuggestionDto.setDescription(description)
        activitySuggestionDto.setRegion(ACTIVITY_REGION_1)
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        when:
        new ActivitySuggestion(activitySuggestionDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() = ErrorMessage.SUGGESTION_DESCRIPTION_LENGTH_INVALID

        where:
        description
        "a"
        "ab"
        "abc"
        "abcd"
        "abcde"
        "abcdef"
        "abcdefg"
        "abcdefgh"
    }

    @Unroll
    def "create activity suggestion and violate name is unique for volunteer invariant"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_1
        institution.getActivities() >> [otherActivity]
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        and: "an activity suggestion dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setParticipantsNumberLimit(1)
        activitySuggestionDto.setName(name)
        activitySuggestionDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activitySuggestionDto.setRegion(ACTIVITY_REGION_1)
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        when:
        new ActivitySuggestion(activitySuggestionDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() = ErrorMessage.SUGGESTION_NAME_UNIQUE_FOR_VOLUNTEER

        where:
        name
        ACTIVITY_NAME_1
        ACTIVITY_NAME_2
    }

    @Unroll
    def "create activity suggestion and violate application deadline must be at least 7 days after creation date"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]

        and: "an activity suggestion dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setParticipantsNumberLimit(1)
        activitySuggestionDto.setName(ACTIVITY_NAME_1)
        activitySuggestionDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activitySuggestionDto.setRegion(ACTIVITY_REGION_1)
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(offsetDate))

        when:
        new ActivitySuggestion(activitySuggestionDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() = ErrorMessage.SUGGESTION_APPLICATION_DEADLINE_INVALID

        where:
        offsetDate
        IN_ONE_DAY
        IN_TWO_DAYS
        IN_THREE_DAYS
        DateHandler.now().plusDays(7)
        DateHandler.now().plusDays(8)
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

