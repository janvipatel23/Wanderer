package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.FutureTrip;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface FutureTripRepository extends JpaRepository<FutureTrip, Integer> {
    List<FutureTrip> findByUserId(int userId);

    List<FutureTrip> findByPinPinId(int pinId);
}
