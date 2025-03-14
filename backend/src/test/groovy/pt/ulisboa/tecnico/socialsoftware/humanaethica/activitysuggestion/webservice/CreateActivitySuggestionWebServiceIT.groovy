package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateActivitySuggestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activitySuggestionDto
    def institutionId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        institutionId = institution.getId()
        def volunteer = userService.getDemoVolunteer()

        activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TWELVE_DAYS)
    }

    def "login as volunteer, and create an activity suggestion"() {
        given:
        demoVolunteerLogin()        // access condition: volunteer

        when:
        def response = webClient.post()
                .uri('/activitysuggestions/' + institutionId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response data"
        response.name == ACTIVITY_NAME_1
        response.region == ACTIVITY_REGION_1
        response.participantsNumberLimit == 1
        response.description == ACTIVITY_DESCRIPTION_1
        response.startingDate == DateHandler.toISOString(IN_NINE_DAYS)
        response.endingDate == DateHandler.toISOString(IN_TWELVE_DAYS)
        response.applicationDeadline == DateHandler.toISOString(IN_EIGHT_DAYS)
        and: "check database"
        activitySuggestionRepository.count() == 1
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

