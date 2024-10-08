package com.raczkowski.app.article;

import com.raczkowski.app.comment.Comment;
import com.raczkowski.app.enums.ArticleStatus;
import com.raczkowski.app.likes.ArticleLike;
import com.raczkowski.app.user.AppUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Article {
    @SequenceGenerator(
            name = "article_sequence",
            sequenceName = "article_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "article_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private ZonedDateTime postedDate;

    @OneToOne
    @JoinColumn(
            nullable = false
    )
    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.APPROVED;

    private int likesNumber = 0;

    private ZonedDateTime updatedAt;

    private boolean isUpdated = false;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    private List<ArticleLike> articleLikes = new ArrayList<>();

    private ZonedDateTime acceptedAt;

    @OneToOne
    private AppUser acceptedBy;

    @Column(columnDefinition = "boolean default false")
    private boolean isPinned;

    public Article(
            String title,
            String content,
            ZonedDateTime postedDate,
            AppUser appUser
    ) {
        this.title = title;
        this.content = content;
        this.postedDate = postedDate;
        this.appUser = appUser;
    }

    public Article(
            String title,
            String content,
            ZonedDateTime postedDate,
            AppUser appUser,
            ZonedDateTime acceptedAt,
            AppUser acceptedBy
    ) {
        this.title = title;
        this.content = content;
        this.postedDate = postedDate;
        this.appUser = appUser;
        this.acceptedAt = acceptedAt;
        this.acceptedBy = acceptedBy;
    }
}
