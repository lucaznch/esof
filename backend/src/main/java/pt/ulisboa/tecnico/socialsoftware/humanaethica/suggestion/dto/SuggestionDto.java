package pt.ulisboa.tecnico.socialsoftware.humanaethica.suggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class SuggestionDto {
    private Integer id;
    private Integer participantsNumberLimit;
    private String name;
    private String description;
    private String region;
    private String creationDate;
    private String startingDate;
    private String endingDate;
    private String applicationDeadline;
    private String state;
    private InstitutionDto institution;
    private UserDto volunteer;

    public SuggestionDto() {
    }

    public SuggestionDto(Suggestion suggestion, boolean deepCopyInstitution, boolean deepCopyVolunteer) {
        setId(suggestion.getId());
        setParticipantsNumberLimit(suggestion.getParticipantsNumberLimit());
        setName(suggestion.getName());
        setDescription(suggestion.getDescription());
        setRegion(suggestion.getRegion());
        setCreationDate(DateHandler.toISOString(suggestion.getCreationDate()));
        setStartingDate(DateHandler.toISOString(suggestion.getStartingDate()));
        setEndingDate(DateHandler.toISOString(suggestion.getEndingDate()));
        setApplicationDeadline(DateHandler.toISOString(suggestion.getApplicationDeadline()));
        setState(suggestion.getState().name());

        if (deepCopyInstitution && (suggestion.getInstitution() != null)) {
            setInstitution(new InstitutionDto(suggestion.getInstitution(), false, false));
        }
        if (deepCopyVolunteer && (suggestion.getVolunteer() != null)) {
            setVolunteer(new UserDto(suggestion.getVolunteer()));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }

    public UserDto getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
                "id=" + id +
                ", participantsNumberLimit=" + participantsNumberLimit +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", region='" + region + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", startingDate='" + startingDate + '\'' +
                ", endingDate='" + endingDate + '\'' +
                ", applicationDeadline='" + applicationDeadline + '\'' +
                ", state='" + state + '\'' +
                ", institution=" + institution +
                ", volunteer=" + volunteer +
                '}';
    }
}
