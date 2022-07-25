package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.PinRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinRatingRepository extends JpaRepository<PinRating, Integer> {

    @Query(value = "SELECT * FROM pin_ratings WHERE pin_id = ?1", nativeQuery = true)
    List<PinRating> findAllByPinId(Integer pinId);

    @Query(value = "SELECT * FROM pin_ratings WHERE user_id = ?1 AND pin_id = ?2", nativeQuery = true)
    PinRating findPinRatingByUserIdAndPinId(Integer userId, Integer pinID);
}