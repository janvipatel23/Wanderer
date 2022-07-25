package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.PinComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinCommentRepository extends JpaRepository<PinComment, Integer> {

    @Query(value = "SELECT * FROM pin_comments WHERE pin_id = ?1", nativeQuery = true)
    List<PinComment> findAllByPinId(Integer pinId);
}