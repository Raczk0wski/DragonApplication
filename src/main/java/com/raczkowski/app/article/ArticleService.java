package com.raczkowski.app.article;

import com.raczkowski.app.comment.Comment;
import com.raczkowski.app.comment.CommentRepository;
import com.raczkowski.app.comment.CommentService;
import com.raczkowski.app.common.MetaData;
import com.raczkowski.app.common.PageResponse;
import com.raczkowski.app.dto.ArticleDto;
import com.raczkowski.app.dtoMappers.ArticleDtoMapper;
import com.raczkowski.app.exceptions.Exception;
import com.raczkowski.app.likes.ArticleLike;
import com.raczkowski.app.likes.ArticleLikeRepository;
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

import javax.transaction.Transactional;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    public String create(ArticleRequest request) {
        if (
                request.getTitle() == null
                        || request.getContent() == null
                        || request.getTitle().equals("")
                        || request.getContent().equals("")
        ) {
            throw new Exception("Title or content can't be empty");
        }
        AppUser user = userService.getLoggedUser();
        articleRepository.save(new Article(
                request.getTitle(),
                request.getContent(),
                ZonedDateTime.now(ZoneOffset.UTC),
                user
        ));
        userRepository.updateArticlesCount(user.getId());
        return "saved";
    }

    public PageResponse<ArticleDto> getAllArticles(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        Page<Article> articlePage = articleRepository.findAll(pageable);
        AppUser user = userService.getLoggedUser();

        return new PageResponse<>(
                articlePage
                        .stream()
                        .map(article -> ArticleDtoMapper.articleDtoMapperWithAdditionalFields(
                                article,
                                isArticleLiked(article, user),
                                commentService.getNumberCommentsOfArticle(article.getId())
                        ))
                        .toList(),
                new MetaData(
                        articlePage.getTotalElements(),
                        articlePage.getTotalPages(),
                        articlePage.getNumber() + 1,
                        articlePage.getSize()));
    }

    public ArticleDto getMostLikableArticle() {
        return ArticleDtoMapper.articleDtoMapper(articleRepository.getFirstByOrderByLikesNumberDesc());
    }

    public List<ArticleDto> getArticlesFromUser(Long userID) {
        return articleRepository
                .findAllByAppUser(userRepository.findById(userID))
                .stream()
                .map(ArticleDtoMapper::articleDtoMapper)
                .collect(Collectors.toList());
    }
    @Transactional
    public String removeArticle(Long id) {
        Article article = articleRepository.findArticleById(id);
        if (!article.getAppUser().getId().equals(userService.getLoggedUser().getId())) {
            throw new Exception("User doesn't have permission to remove this article");
        }

        articleLikeRepository.deleteArticleLikesByArticle(article);

        for (Comment comment : article.getComments()) {
            commentLikeRepository.deleteCommentLikesByComment(comment);
        }

        articleRepository.deleteById(id);
        return "Removed";
    }

    public ArticleDto getArticleByID(Long id) {
        Article article = articleRepository.findArticleById(id);
        if (article == null) {
            throw new Exception("There is no article with provided id");
        }
        return ArticleDtoMapper.articleDtoMapper(articleRepository.findArticleById(id));
    }

    public void likeArticle(Long id) {
        AppUser user = userService.getLoggedUser();

        Article article = articleRepository.findArticleById(id);
        if (article == null) {
            throw new Exception("Article doesnt exists");
        }

        if (!articleLikeRepository.existsArticleLikesByAppUserAndArticle(user, article)) {
            articleLikeRepository.save(new ArticleLike(user, article, true));
            articleRepository.updateArticleLikes(id, 1);
        } else {
            articleLikeRepository.delete(articleLikeRepository.findByArticleAndAppUser(article, user));
            articleRepository.updateArticleLikes(id, -1);
        }
    }

    public void updateArticle(ArticleRequest articleRequest) {
        if ((articleRequest.getTitle() == null || articleRequest.getTitle().equals("")) &&
                (articleRequest.getContent() == null || articleRequest.getContent().equals(""))) {
            throw new Exception("Title or content can't be empty");
        }

        Article article = articleRepository.findArticleById(articleRequest.getId());

        if (article == null) {
            throw new Exception("There is no article with provided id:" + articleRequest.getId());
        } else if (!article.getAppUser().getId().equals(userService.getLoggedUser().getId())) {
            throw new Exception("User doesn't have permission to update this comment");
        }

        if (articleRequest.getTitle() == null) {
            articleRepository.updateArticle(
                    articleRequest.getId(),
                    article.getTitle(),
                    articleRequest.getContent(),
                    ZonedDateTime.now(ZoneOffset.UTC)
            );
        } else if (articleRequest.getContent() == null) {
            articleRepository.updateArticle(
                    articleRequest.getId(),
                    articleRequest.getTitle(),
                    article.getContent(),
                    ZonedDateTime.now(ZoneOffset.UTC)
            );
        } else {
            articleRepository.updateArticle(
                    articleRequest.getId(),
                    articleRequest.getTitle(),
                    articleRequest.getContent(),
                    ZonedDateTime.now(ZoneOffset.UTC));
        }
    }

    private boolean isArticleLiked(Article article, AppUser user) {
        return articleLikeRepository.existsArticleLikesByAppUserAndArticle(user, article);
    }
}
