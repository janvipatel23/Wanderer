package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<Blog, Integer> {

    @Query(value = "SELECT * FROM blog ORDER BY blog_id DESC", nativeQuery = true)
    List<Blog> findAll();

    // custom query to find all blogs by User ID
    @Query(value = "SELECT * FROM blog WHERE user_id = ?1 ORDER BY blog_id DESC", nativeQuery = true)
    List<Blog> findAllByUserId(int userId);
}