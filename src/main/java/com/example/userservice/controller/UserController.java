package com.example.userservice.controller;


import com.example.userservice.dto.*;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(params = "userId")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    public ResponseEntity<UserProfileResponseDto> userById(@RequestParam(value = "userId", required = true) int userId) {
        return userService.getUserById(userId);
    }

    @PostMapping(value = "/create-user", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> createNewUser(@RequestPart("image") MultipartFile image,
                                                @RequestPart("data") UserNoImageRequestDto data) {
        return userService.saveUser(image, data);
    }

    @PostMapping(value = "/login-user")
    public ResponseEntity<?> loginUser (@RequestBody LoginUserDto loginUserDto) {
        return userService.loginUser(loginUserDto);
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseDto<UserSearchResponseDto> searchUser(@RequestParam(value = "name", required = true) String name,
                                                         @RequestParam(value = "page", required = true) int page) {
        return userService.searchBusinessUser(name, page);
    }

    // TODO
    @GetMapping(params = "postId")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    public ResponseEntity<UserDto> getUserById(@RequestParam(value = "postId", required = true) int postId) {
        return userService.getUserByPostId(postId);
    }

    // TODO
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> likeUser(@RequestBody UserDto data) {
        return userService.saveLikedUser(data);
    }
}
