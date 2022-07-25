package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.BucketListPin;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BucketListRepository extends JpaRepository<BucketListPin, Integer> {

    @Query(value = "SELECT * from bucket_list where user_id = ?1", nativeQuery = true)
    List<BucketListPin> findAllByUser(Integer userId);

    // query to delete entry with pinId and userId
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM bucket_list WHERE pin_id = ?1 AND user_id = ?2", nativeQuery = true)
    void deleteByPinIdAndUserId(Integer pinId, Integer userId);

    // find entry with pinId and userId
    @Query(value = "SELECT * FROM bucket_list WHERE pin_id = ?1 AND user_id = ?2 LIMIT 1", nativeQuery = true)
    BucketListPin findByPinIdAndUserId(Integer pinId, Integer userId);
}
