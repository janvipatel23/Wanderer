package ca.dal.cs.wanderer.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "bucket_list")

public class BucketListPin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_list_id", unique = true, nullable = false)
    private Integer bucketListId;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "pin_id", nullable = false)
    private Pin pin;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BucketListPin(){
    }

    public BucketListPin(User user, Pin pin) {
        this.user = user;
        this.pin = pin;
    }

}
