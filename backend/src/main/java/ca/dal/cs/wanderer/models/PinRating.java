package ca.dal.cs.wanderer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pin_ratings")
public class PinRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pin_rating_id", nullable = false)
    private Integer ratingId;
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    // create many-to-one relationship with Pin
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pin_id", nullable = false)
    private Pin pin;
    @Column(name = "rating")
    private Integer rating;

    public PinRating(Integer userId, Pin pin, Integer rating) {
        this.userId = userId;
        this.pin = pin;
        this.rating = rating;
    }

    public PinRating(Integer userId, Integer rating) {
        this.userId = userId;
        this.rating = rating;
    }
}
