package com.raczkowski.app.likes;

import com.raczkowski.app.User.AppUser;
import com.raczkowski.app.article.Article;
import com.raczkowski.app.comment.Comment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class CommentLike {
    @SequenceGenerator(
            name = "like_sequence",
            sequenceName = "like_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "like_sequence"
    )
    private Long id;

    @OneToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private AppUser appUser;

    @OneToOne
    @JoinColumn(
            nullable = false,
            name = "comment_id"
    )
    private Comment comment;

    @JoinColumn(
            nullable = false,
            name = "isLiked"
    )
    private boolean isLiked;


    public CommentLike(AppUser appUser, Comment comment, boolean isLiked) {
        this.appUser = appUser;
        this.comment = comment;
        this.isLiked = isLiked;
    }
}
