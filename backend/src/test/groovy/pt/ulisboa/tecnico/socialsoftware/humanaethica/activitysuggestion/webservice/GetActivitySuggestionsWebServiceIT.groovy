package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetActivitySuggestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institutionId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        
        def institution = institutionService.getDemoInstitution()
        institutionId = institution.getId()
        def volunteer = userService.getDemoVolunteer()

        given: "activity suggestion info"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TWELVE_DAYS)
    
        and: "an activity suggestion"
        def activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)

        and: "another activity suggestion"
        activitySuggestionDto.name = ACTIVITY_NAME_2
        activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)
    }


    def "get activity suggestions"() {
        given:
        demoMemberLogin()       // access condition: member

        when:
        def response = webClient.get()
                .uri('/activitysuggestions/' + institutionId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).name == ACTIVITY_NAME_1
        response.get(1).name == ACTIVITY_NAME_2
        response.get(0).region == ACTIVITY_REGION_1
        response.get(0).participantsNumberLimit == 1
        response.get(0).description == ACTIVITY_DESCRIPTION_1
        DateHandler.toLocalDateTime(response.get(0).startingDate).withNano(0) == IN_NINE_DAYS.withNano(0)
        DateHandler.toLocalDateTime(response.get(0).endingDate).withNano(0) == IN_TWELVE_DAYS.withNano(0)
        DateHandler.toLocalDateTime(response.get(0).applicationDeadline).withNano(0) == IN_EIGHT_DAYS.withNano(0)
        and: "check database"
        activitySuggestionRepository.count() == 2
        def activitySuggestion = activitySuggestionRepository.findAll().get(0)
        activitySuggestion.getName() == ACTIVITY_NAME_1
        activitySuggestion.getRegion() == ACTIVITY_REGION_1
        activitySuggestion.getParticipantsNumberLimit() == 1
        activitySuggestion.getDescription() == ACTIVITY_DESCRIPTION_1
        activitySuggestion.getStartingDate().withNano(0) == IN_NINE_DAYS.withNano(0)
        activitySuggestion.getEndingDate().withNano(0) == IN_TWELVE_DAYS.withNano(0)
        activitySuggestion.getApplicationDeadline().withNano(0) == IN_EIGHT_DAYS.withNano(0)

        cleanup:
        deleteAll()
    }
}

