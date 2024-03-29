package com.raczkowski.app.dto;

import com.raczkowski.app.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private UserRole userRole;

    private int articlesCount;

    private int commentsCount;
}
