package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;

@Repository
@Transactional
public interface ActivitySuggestionRepository extends JpaRepository<ActivitySuggestion, Integer> {
    @Query("SELECT a FROM ActivitySuggestion a WHERE a.institution.id = :institutionId")
    List<ActivitySuggestion> getActivitiesByInstitutionId(Integer institutionId);


}

