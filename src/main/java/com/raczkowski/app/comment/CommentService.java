package com.raczkowski.app.comment;

import com.raczkowski.app.article.ArticleRepository;
import com.raczkowski.app.common.MetaData;
import com.raczkowski.app.common.PageResponse;
import com.raczkowski.app.dto.CommentDto;
import com.raczkowski.app.dtoMappers.CommentDtoMapper;
import com.raczkowski.app.exceptions.Exception;
import com.raczkowski.app.likes.CommentLike;
import com.raczkowski.app.likes.CommentLikeRepository;
import com.raczkowski.app.user.AppUser;
import com.raczkowski.app.user.UserRepository;
import com.raczkowski.app.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public PageResponse<CommentDto> getAllCommentsFromArticle(Long id, int pageNumber, int pageSize, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), "likesNumber"));
        Page<Comment> commentPage = commentRepository.getAllByArticleId(id, pageable);

        return new PageResponse<>(
                commentPage
                        .stream()
                        .map(CommentDtoMapper::commentDtoMapper)
                        .toList(),
                new MetaData(
                        commentPage.getTotalElements(),
                        commentPage.getTotalPages(),
                        commentPage.getNumber() + 1,
                        commentPage.getSize()
                ));
    }

    public String addComment(CommentRequest commentRequest) {
        if (commentRequest.getContent().equals("")) {
            throw new Exception("Comment can't be empty");
        }

        AppUser user = userService.getLoggedUser();

        if (!articleRepository.existsById(commentRequest.getId())) {
            throw new Exception("Article with this id doesnt exists");
        } else {
            commentRepository.save(new Comment(
                    commentRequest.getContent(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    user,
                    articleRepository.findArticleById(commentRequest.getId()
                    )));
            userRepository.updateCommentsCount(user.getId());
        }
        return "Added";
    }

    public void likeComment(Long id) {
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) {
            throw new Exception("Comment doesnt exists");
        }

        if (!commentLikeRepository.existsCommentLikeByAppUserAndComment(userService.getLoggedUser(), comment)) {
            commentLikeRepository.save(new CommentLike(userService.getLoggedUser(), comment, true));
            commentRepository.updateCommentLikes(id);
        } else {
            throw new Exception("Already liked");
        }
    }

    public String removeComment(Long id) {
        Comment comment = commentRepository.findCommentById(id);
        if (comment != null && !comment.getAppUser().getId().equals(userService.getLoggedUser().getId())) {
            throw new Exception("User doesn't have permission to remove this comment");
        }
        commentRepository.deleteById(id);
        return "Removed";
    }

    public String updateComment(CommentRequest commentRequest) {
        if (commentRequest.getContent() == null || commentRequest.getContent().equals("")) {
            throw new Exception("Comment can't be empty");
        }

        Comment comment = commentRepository.findCommentById(commentRequest.getId());

        if (comment == null) {
            throw new Exception("There is no comment with provided id:" + commentRequest.getId());
        } else if (!comment.getAppUser().getId().equals(userService.getLoggedUser().getId())) {
            throw new Exception("User doesn't have permission to update this comment");
        }

        commentRepository.updateCommentContent(
                commentRequest.getId(),
                commentRequest.getContent(),
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return "Updated";
    }
}
