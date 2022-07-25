package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {

    @Query(value = "SELECT * FROM blog_comments WHERE blog_id = ?1", nativeQuery = true)
    List<BlogComment> findAllByBlogId(Integer blogId);
}
