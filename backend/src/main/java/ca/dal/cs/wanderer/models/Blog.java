package ca.dal.cs.wanderer.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id", nullable = false)
    private Integer blogId;

    @Lob
    @Column(name = "blog_description", nullable = false, columnDefinition = "TEXT")
    private String blogDescription;

    @Column(name = "blog_title", nullable = false)
    private String blogTitle;

    @Column(name = "blog_creation_date")
    private LocalDate blogCreationDate;

    @Column(name = "blog_author", nullable = false)
    private String blogAuthor;

    @Lob
    @Column(name = "blog_image", columnDefinition="MEDIUMBLOB")
    private byte[] blogImage;

    // create many-to-one relationship with User
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // create one-to-many relationship with PinRating
    @JsonManagedReference
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BlogComment> blogComments;

    public Blog(String blogDescription, String blogTitle, String blogAuthor, byte[] blogImage, LocalDate blogCreationDate, User user) {
        this.blogDescription = blogDescription;
        this.blogTitle = blogTitle;
        this.blogAuthor = blogAuthor;
        this.blogImage = blogImage;
        this.blogCreationDate = blogCreationDate;
        this.user = user;
    }

    public void addBlogComment(BlogComment blogComment) {
        blogComments.add(blogComment);
    }
}
