package com.raczkowski.app.dtoMappers;

import com.raczkowski.app.admin.moderation.article.ArticleToConfirm;
import com.raczkowski.app.admin.moderation.article.RejectedArticle;
import com.raczkowski.app.article.Article;
import com.raczkowski.app.article.DeletedArticle;
import com.raczkowski.app.dto.*;
import org.springframework.stereotype.Component;

@Component
public class ArticleDtoMapper {
    public static ArticleDto articleDtoMapper(Article article, int likesCount) {
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getPostedDate(),
                likesCount,
                new AuthorDto(
                        article.getAppUser().getId(),
                        article.getAppUser().getFirstName(),
                        article.getAppUser().getLastName(),
                        article.getAppUser().getEmail(),
                        article.getAppUser().isAccountBlocked()
                ),
                article.getUpdatedAt(),
                article.isUpdated(),
                article.getStatus(),
                article.getAcceptedAt(),
                new AuthorDto(
                        article.getAcceptedBy().getId(),
                        article.getAcceptedBy().getFirstName(),
                        article.getAcceptedBy().getLastName(),
                        article.getAcceptedBy().getEmail(),
                        article.getAcceptedBy().isAccountBlocked()
                )
        );
    }

    public static RejectedArticleDto rejectedArticleDtoMapper(RejectedArticle article) {
        return new RejectedArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getPostedDate(),
                new AuthorDto(
                        article.getAppUser().getId(),
                        article.getAppUser().getFirstName(),
                        article.getAppUser().getLastName(),
                        article.getAppUser().getEmail(),
                        article.getAppUser().isAccountBlocked()
                ),
                article.getStatus(),
                article.getRejectedAt(),
                new AuthorDto(
                        article.getRejectedBy().getId(),
                        article.getRejectedBy().getFirstName(),
                        article.getRejectedBy().getLastName(),
                        article.getRejectedBy().getEmail(),
                        article.getRejectedBy().isAccountBlocked()
                )
        );
    }

    public static NonConfirmedArticleDto nonConfirmedArticleMapper(ArticleToConfirm article) {
        return new NonConfirmedArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getPostedDate(),
                new AuthorDto(
                        article.getAppUser().getId(),
                        article.getAppUser().getFirstName(),
                        article.getAppUser().getLastName(),
                        article.getAppUser().getEmail(),
                        article.getAppUser().isAccountBlocked()
                ),
                article.getStatus()
        );
    }

    public static DeletedArticleDto deletedArticle(DeletedArticle article) {
        return new DeletedArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getPostedDate(),
                article.getLikesNumber(),
                new AuthorDto(
                        article.getAppUser().getId(),
                        article.getAppUser().getFirstName(),
                        article.getAppUser().getLastName(),
                        article.getAppUser().getEmail(),
                        article.getAppUser().isAccountBlocked()
                ),
                article.getUpdatedAt(),
                article.isUpdated(),
                article.getStatus(),
                article.getAcceptedAt(),
                new AuthorDto(
                        article.getAcceptedBy().getId(),
                        article.getAcceptedBy().getFirstName(),
                        article.getAcceptedBy().getLastName(),
                        article.getAcceptedBy().getEmail(),
                        article.getAcceptedBy().isAccountBlocked()
                ),
                article.getDeletedAt(),
                new AuthorDto(
                        article.getDeletedBy().getId(),
                        article.getDeletedBy().getFirstName(),
                        article.getDeletedBy().getLastName(),
                        article.getDeletedBy().getEmail(),
                        article.getDeletedBy().isAccountBlocked()
                )
        );
    }

    public static ArticleDto articleDtoMapperWithAdditionalFieldsMapper(Article article, boolean isLiked, int commentsNumber, int likesCount) {
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getPostedDate(),
                likesCount,
                new AuthorDto(
                        article.getAppUser().getId(),
                        article.getAppUser().getFirstName(),
                        article.getAppUser().getLastName(),
                        article.getAppUser().getEmail(),
                        article.getAppUser().isAccountBlocked()
                ),
                article.getUpdatedAt(),
                article.isUpdated(),
                article.getStatus(),
                isLiked,
                commentsNumber,
                article.getAcceptedAt(),
                new AuthorDto(
                        article.getAcceptedBy().getId(),
                        article.getAcceptedBy().getFirstName(),
                        article.getAcceptedBy().getLastName(),
                        article.getAcceptedBy().getEmail(),
                        article.getAcceptedBy().isAccountBlocked()
                ),
                article.isPinned()
        );
    }
}
