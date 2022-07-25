package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.PinImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PinImageRepo extends JpaRepository<PinImage, Integer> {

    @Transactional
    @Modifying
    @Query(value = "delete from image where pin_id = :pin_id", nativeQuery = true)
    public void deleteAllByPinId(@Param("pin_id") int pinId);
}
