package com.raczkowski.app.comment;

import com.raczkowski.app.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> getAllByArticleId(Long articleId, Pageable pageable);

    List<Comment> findAllById(Comment comment);

    Comment findCommentById(Long id);

    List<Comment> getCommentsByArticle(Article article);

    void deleteCommentsByArticle(Article article);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c " +
            "SET c.likesNumber = c.likesNumber + :amount " +
            "WHERE c.id = :id")
    void updateCommentLikes(@Param("id") Long id, @Param("amount") int amount);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c " +
            "SET c.content = :content, c.updatedAt = :zonedDateTime, c.isUpdated = true WHERE c.id = :id")
    void updateCommentContent(@Param("id") Long id, @Param("content") String content, ZonedDateTime zonedDateTime);
}
