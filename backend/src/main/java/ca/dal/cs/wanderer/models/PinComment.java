package ca.dal.cs.wanderer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pin_comments")
public class PinComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pin_comment_id", nullable = false)
    private Integer pinCommentId;
    // create many-to-one relationship with User
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    // create many-to-one relationship with Pin
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pin_id", nullable = false)
    private Pin pin;
    @Column(name = "pin_comments")
    private String comment;
    @Column(name = "pin_comment_date")
    private LocalDate date;

    public PinComment(User user, String comment, LocalDate date) {
        this.user = user;
        this.comment = comment;
        this.date = date;
    }
}
