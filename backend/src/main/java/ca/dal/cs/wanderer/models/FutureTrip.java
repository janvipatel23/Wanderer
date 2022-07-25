package ca.dal.cs.wanderer.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Data
@Table(name="future_trips")
public class FutureTrip {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "trip_id", unique = true, nullable = false)
    private Integer tripId;

    @Column(name = "future_trip_name")
    private String tripName;

    @Column(name = "future_trip_description")
    private String tripDescription;

    @Column(name = "future_trip_date_time")
    private Calendar tripDate;

    @OneToOne()
    @JoinColumn(name = "pin_id", referencedColumnName = "pin_id")
    private Pin pin;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
