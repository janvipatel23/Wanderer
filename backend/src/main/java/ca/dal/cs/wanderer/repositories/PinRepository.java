package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.Pin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PinRepository extends JpaRepository<Pin, Integer> {
    @Query(value = "SELECT pin_id as pinId, user_id as userId, pin_latitude as latitude, pin_longitude as longitude, pin_location_name as locationName, ( 6371 * acos( cos( radians(:centerLat) ) * cos( radians( pin_latitude ) ) \n" +
            "* cos( radians( pin_longitude ) - radians(:centerLng) ) + sin( radians(:centerLat) ) * sin(radians(pin_latitude)) ) ) AS distance \n" +
            "FROM pin_information\n" +
            "HAVING distance < :radius \n" +
            "ORDER BY distance ", nativeQuery = true)
    public List<PinBasicInfo> getPinsByRadius(@Param("radius") double radius, @Param("centerLat") double centerLat, @Param("centerLng") double centerLng);

    public static interface PinBasicInfo {
        public Integer getPinId();
        public Integer getUserId();
        public Double getLatitude();
        public Double getLongitude();
        public String getLocationName();
    }
}