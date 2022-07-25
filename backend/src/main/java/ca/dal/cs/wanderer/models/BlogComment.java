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
@Table(name = "blog_comments")
public class BlogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_comment_id", nullable = false)
    private Integer blogCommentId;
    // create many-to-one relationship with User
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    // create many-to-one relationship with Blog
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;
    @Column(name = "blog_comments")
    private String comment;
    @Column(name = "blog_comment_date")
    private LocalDate date;

    public BlogComment(User user, String comment, LocalDate date) {
        this.user = user;
        this.comment = comment;
        this.date = date;
    }
}
