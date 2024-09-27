package com.raczkowski.app.comment;

import com.raczkowski.app.article.ArticleRepository;
import com.raczkowski.app.common.GenericService;
import com.raczkowski.app.common.MetaData;
import com.raczkowski.app.common.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    public PageResponse<CommentDto> getAllCommentsFromArticle(Long id, int pageNumber, int pageSize) { //TODO: do poprawy aby pobierało komentarzy z danego artykułu
        Page<Comment> commentPage = GenericService
                .pagination(
                        commentRepository,
                        pageNumber,
                        pageSize,
                        "likesNumber",
                        "desc"
                );

        AppUser user = userService.getLoggedUser();

        return new PageResponse<>(
                commentPage
                        .stream()
                        .map(comment -> CommentDtoMapper.commentDtoMapperWithAdditionalFields(comment, isCommentLiked(comment, user)))
                        .toList(),
                new MetaData(
                        commentPage.getTotalElements(),
                        commentPage.getTotalPages(),
                        commentPage.getNumber() + 1,
                        commentPage.getSize()
                ));
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
            userRepository.updateCommentsCount(user.getId());
        }
        return CommentDtoMapper.commentDtoMapper(comment);
    }

    public void createComment(Comment comment){
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
            commentRepository.updateCommentLikes(id, 1);
        } else {
            commentLikeRepository.delete(commentLikeRepository.findByCommentAndAppUser(comment, user));
            commentRepository.updateCommentLikes(id, -1);
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
}
