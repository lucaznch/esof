package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion


@DataJpaTest
class GetActivitySuggestionsByInstitutionServiceTest extends SpockTest {
    def setup() {
        def institution = institutionService.getDemoInstitution()
        def volunteer = userService.getDemoVolunteer()

        given: "activity suggestion info"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TWELVE_DAYS)

        and: "an activity suggestion"
        System.out.println("a")
        def activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)
        System.out.println("b")

        and: 'another activity suggestion'
        System.out.println("c")
        activitySuggestionDto.name = ACTIVITY_NAME_2
        activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)
        System.out.println("d")
    }

    def 'get two activity suggestions'() {
        when:
        def result = activitySuggestionService.getActivitySuggestionsByInstitution(institution.getId())

        then:
        result.size() == 2
        result.get(0).name == ACTIVITY_NAME_1
        result.get(1).name == ACTIVITY_NAME_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

