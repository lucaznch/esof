package pt.ulisboa.tecnico.socialsoftware.humanaethica.suggestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.suggestion.domain.Suggestion;

@Repository
@Transactional
public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {

}

