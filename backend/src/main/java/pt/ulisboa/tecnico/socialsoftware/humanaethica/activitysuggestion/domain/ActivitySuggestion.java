package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "suggestion")
public class ActivitySuggestion {
    public enum State {APPROVED, REJECTED, IN_REVIEW}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer participantsNumberLimit;
    private String name;
    private String description;
    private String region;
    private LocalDateTime creationDate;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private LocalDateTime applicationDeadline;

    @Enumerated(EnumType.STRING)
    private ActivitySuggestion.State state = ActivitySuggestion.State.IN_REVIEW;    // default state

    @ManyToOne
    private Institution institution;

    @ManyToOne
    private Volunteer volunteer;

    public ActivitySuggestion() {
    }

    public ActivitySuggestion(ActivitySuggestionDto suggestionDto, Institution institution, Volunteer volunteer) {
        setParticipantsNumberLimit(suggestionDto.getParticipantsNumberLimit());
        setName(suggestionDto.getName());
        setDescription(suggestionDto.getDescription());
        setRegion(suggestionDto.getRegion());
        setCreationDate(DateHandler.now());
        setStartingDate(DateHandler.toLocalDateTime(suggestionDto.getStartingDate()));
        setEndingDate(DateHandler.toLocalDateTime(suggestionDto.getEndingDate()));
        setApplicationDeadline(DateHandler.toLocalDateTime(suggestionDto.getApplicationDeadline()));
        setState(ActivitySuggestion.State.valueOf(suggestionDto.getState()));
        setInstitution(institution);
        setVolunteer(volunteer);

        verifyInvariants();
    }

    public Integer getId() {
        return id;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDateTime startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDateTime endingDate) {
        this.endingDate = endingDate;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public ActivitySuggestion.State getState() {
        return state;
    }

    public void setState(ActivitySuggestion.State state) {
        this.state = state;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addActivitySuggestion(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addActivitySuggestion(this);
    }

    private void verifyInvariants() {
        nameIsRequired();
        regionIsRequired();
        descriptionIsRequired();
        applicationDeadlineIsRequired();
        startingDateIsRequired();
        endingDateIsRequired();
        applicationBeforeStartDate();
        startBeforeEnd();
        descriptionLength();
        nameIsUniqueForVolunteer();
        applicationDeadlineAfterCreation();
    }

    private void nameIsRequired() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new HEException(SUGGESTION_NAME_INVALID, this.name);
        }
    }

    private void regionIsRequired() {
        if (this.region == null || this.region.trim().isEmpty()) {
            throw new HEException(SUGGESTION_REGION_NAME_INVALID, this.region);
        }
    }

    private void descriptionIsRequired() {
        if (this.description == null || this.description.trim().isEmpty()) {
            throw new HEException(SUGGESTION_DESCRIPTION_INVALID, this.description);
        }
    }

    private void applicationDeadlineIsRequired() {
        if (this.applicationDeadline == null) {
            throw new HEException(SUGGESTION_INVALID_DATE, "Enrollment deadline");
        }
    }

    private void startingDateIsRequired() {
        if (this.startingDate == null) {
            throw new HEException(SUGGESTION_INVALID_DATE, "Starting date");
        }
    }

    private void endingDateIsRequired() {
        if (this.endingDate == null) {
            throw new HEException(SUGGESTION_INVALID_DATE, "Ending date");
        }
    }

    private void applicationBeforeStartDate() {
        if (!this.applicationDeadline.isBefore(this.startingDate)) {
            throw new HEException(SUGGESTION_APPLICATION_DEADLINE_AFTER_START);
        }
    }

    private void startBeforeEnd() {
        if (!this.startingDate.isBefore(this.endingDate)) {
            throw new HEException(SUGGESTION_START_AFTER_END);
        }
    }

    private void descriptionLength() {
        if (this.description.length() < 10) {
            throw new HEException(SUGGESTION_DESCRIPTION_LENGTH_INVALID);
        }
    }

    private void nameIsUniqueForVolunteer() {       
        if (this.volunteer.getActivitySuggestions().stream().anyMatch(s -> s.getName().equals(this.name))) {
            throw new HEException(SUGGESTION_NAME_UNIQUE_FOR_VOLUNTEER, this.name);
        }
    }

    private void applicationDeadlineAfterCreation() {
        if (this.creationDate != null && this.applicationDeadline != null) {
            if (!this.applicationDeadline.isAfter(this.creationDate.plusDays(7))) {
                throw new HEException(SUGGESTION_APPLICATION_DEADLINE_AFTER_CREATION);
            }
        }
    }
}
