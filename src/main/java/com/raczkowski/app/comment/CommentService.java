package com.raczkowski.app.comment;

import com.raczkowski.app.article.Article;
import com.raczkowski.app.article.ArticleRepository;
import com.raczkowski.app.dto.CommentDto;
import com.raczkowski.app.dtoMappers.CommentDtoMapper;
import com.raczkowski.app.enums.UserRole;
import com.raczkowski.app.exceptions.ResponseException;
import com.raczkowski.app.likes.CommentLike;
import com.raczkowski.app.likes.CommentLikeRepository;
import com.raczkowski.app.user.AppUser;
import com.raczkowski.app.user.UserRepository;
import com.raczkowski.app.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentStatisticsService commentStatisticsService;

    public List<CommentDto> getAllCommentsFromArticle(Long id) {
        return commentRepository.getCommentsByArticle(articleRepository.findArticleById(id))
                .stream()
                .map(
                        comment ->
                                CommentDtoMapper.commentDtoMapperWithAdditionalFields(
                                        comment,
                                        isCommentLiked(comment, userService.getLoggedUser()),
                                        commentStatisticsService.getLikesCountForComment(comment))
                ).toList();
    }

    public CommentDto addComment(CommentRequest commentRequest) {

        if (commentRequest.getContent().equals("")) {
            throw new ResponseException("Comment can't be empty");
        }

        Comment comment;
        AppUser user = userService.getLoggedUser();

        if (!articleRepository.existsById(commentRequest.getId())) {
            throw new ResponseException("Article with this id doesnt exists");
        } else {
            comment = new Comment(commentRequest.getContent(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    user,
                    articleRepository.findArticleById(commentRequest.getId()
                    ));
            commentRepository.save(comment);
        }
        return CommentDtoMapper.commentDtoMapper(comment, commentStatisticsService.getLikesCountForComment(comment));
    }

    public void createComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void likeComment(Long id) {
        AppUser user = userService.getLoggedUser();
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) {
            throw new ResponseException("Comment doesnt exists");
        }

        if (!commentLikeRepository.existsCommentLikeByAppUserAndComment(userService.getLoggedUser(), comment)) {
            commentLikeRepository.save(new CommentLike(userService.getLoggedUser(), comment, true));
        } else {
            commentLikeRepository.delete(commentLikeRepository.findByCommentAndAppUser(comment, user));
        }
    }

    public String removeComment(Long id) {
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) {
            throw new ResponseException("Comment doesn't exists");
        }

        AppUser user = userService.getLoggedUser();

        if (!comment.getAppUser().getId().equals(user.getId()) || (!user.getUserRole().equals(UserRole.ADMIN) && !user.getUserRole().equals(UserRole.MODERATOR))) {
            throw new ResponseException("User doesn't have permission to remove this comment");
        }
        commentRepository.deleteById(id);
        return "Removed";
    }

    public String updateComment(CommentRequest commentRequest) {

        if (commentRequest.getContent() == null || commentRequest.getContent().equals("")) {
            throw new ResponseException("Comment can't be empty");
        }

        Comment comment = commentRepository.findCommentById(commentRequest.getId());

        if (comment == null) {
            throw new ResponseException("There is no comment with provided id:" + commentRequest.getId());
        } else if (!comment.getAppUser().getId().equals(userService.getLoggedUser().getId())) {
            throw new ResponseException("User doesn't have permission to update this comment");
        }

        commentRepository.updateCommentContent(
                commentRequest.getId(),
                commentRequest.getContent(),
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return "Updated";
    }

    public int getNumberCommentsOfArticle(Long id) {
        return commentRepository.getCommentsByArticle(articleRepository.findArticleById(id)).size();
    }

    private boolean isCommentLiked(Comment comment, AppUser user) {
        return commentLikeRepository.existsCommentLikeByAppUserAndComment(user, comment);
    }

    public void pinComment(Long id) {
        commentRepository.pinComment(id);
    }

    public List<CommentDto> getAllCommentsForUser(Long id) {
        AppUser user = userService.getUserById(id);

        return commentRepository.getCommentsByAppUser(user).stream()
                .map(
                        comment -> CommentDtoMapper.commentDtoMapperWithAdditionalFields(
                                comment,
                                isCommentLiked(comment, user),
                                commentStatisticsService.getLikesCountForComment(comment)))
                .toList();

    }

    public int getCommentsCountForUser(AppUser appUser) {
        return commentRepository.findAllByAppUser(appUser).size();
    }

    public int getCommentCountForArticle(Article article) {
        return commentRepository.getCommentsByArticle(article).size();
    }
}
