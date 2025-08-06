package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.Hashtag;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    List<Hashtag> findAllByNameIn(List<String> names);

    Optional<Hashtag> findByName(String name);
}
