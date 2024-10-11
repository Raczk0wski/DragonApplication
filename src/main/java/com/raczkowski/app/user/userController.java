package com.raczkowski.app.user;

import com.raczkowski.app.dto.UserDto;
import com.raczkowski.app.dtoMappers.UserDtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class userController {

    private final UserService userService;
    private final UserStatisticsService userStatisticsService;

    @GetMapping("/get")
    ResponseEntity<UserDto> getUserById(@RequestParam Long id){
        AppUser user = userService.getUserById(id);
        return ResponseEntity.ok(UserDtoMapper.userDto(
                user,
                userStatisticsService.getArticlesCount(user),
                userStatisticsService.getCommentsCount(user),
                userStatisticsService.getFollowersCount(user),
                userStatisticsService.getFollowingCount(user)
                ));
    }

    @GetMapping("/get/login")
    ResponseEntity<AppUser> getLoggedUser(){
        return ResponseEntity.ok(userService.getLoggedUser());
    }

    @PostMapping("/{userId}/follow")
    public void followUser(@PathVariable Long userId) {
        userService.followUser(userId);
    }

    @PostMapping("/{userId}/unfollow")
    public void unfollowUser(@PathVariable Long userId) {
        userService.unfollowUser(userId);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowers(userId).stream()
                .map(user -> UserDtoMapper.userDto(
                        user,
                        userStatisticsService.getArticlesCount(user),
                        userStatisticsService.getCommentsCount(user),
                        userStatisticsService.getFollowersCount(user),
                        userStatisticsService.getFollowingCount(user)
                ))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowing(userId).stream()
                .map(user -> UserDtoMapper.userDto(
                        user,
                        userStatisticsService.getArticlesCount(user),
                        userStatisticsService.getCommentsCount(user),
                        userStatisticsService.getFollowersCount(user),
                        userStatisticsService.getFollowingCount(user)
                )).collect(Collectors.toList()));
    }
}
