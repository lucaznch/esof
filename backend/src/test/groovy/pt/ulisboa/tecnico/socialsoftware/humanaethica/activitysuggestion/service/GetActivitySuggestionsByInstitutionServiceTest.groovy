package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.activitysuggestion



@DataJpaTest
class GetActivitySuggestionsByInstitutionServiceTest extends SpockTest {
    def setup() {
        def institution = institutionService.getDemoInstitution()
        given: "activity suggestion info"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITYSUGGESTION_NAME_1,ACTIVITYSUGGESTION_REGION_1,1,ACTIVITYSUGGESTION_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        and: "an activity suggestion"
        def activitySuggestion = new ActivitySuggestion(activitySuggestionDto, institution, themes)
        activitySuggestionRepository.save(activitySuggestion)
        and: 'another activity suggestion'
        activitySuggestionDto.name = ACTIVITYSUGGESTION_NAME_2
        activitySuggestion = new ActivitySuggestion(activitySuggestionDto, institution, themes)
        activitySuggestionRepository.save(activitySuggestion)
    }

    def 'get two activity suggestions'() {
        when:
        def result = activitySuggestionService.getActivitySuggestions()

        then:
        result.size() == 2
        result.get(0).name == ACTIVITYSUGGESTION_NAME_1
        result.get(1).name == ACTIVITYSUGGESTION_NAME_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}







