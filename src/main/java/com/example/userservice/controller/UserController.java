package com.example.userservice.controller;


import com.example.userservice.dto.*;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.error.HttpExceptionResponse;
import com.example.userservice.service.UserService;
import io.swagger.annotations.*;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/user-infor/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    @ApiOperation(value = "Find user by it's ID", nickname = "userByIdOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity<UserProfileResponseDto> userById(
            @ApiParam(value = "Get the user by ID.", required = true) @PathVariable("userId") int userId) {
        return userService.getUserById(userId);
    }

    @PostMapping(value = "/create-user", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    @ApiOperation(value = "Create user", nickname = "createUserOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity createNewUser(
            @ApiParam(value = "User profile picture.") @RequestPart("image") MultipartFile image,
            @ApiParam(value = "User data for create user") @RequestPart("data") UserNoImageRequestDto data) {
        return userService.saveUser(image, data);
    }

    @PostMapping(value = "/login-user")
    @ApiOperation(value = "Login user", nickname = "loginUserOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity<?> loginUser (@ApiParam(value = "User credentials") @RequestBody LoginUserDto loginUserDto) {
        return userService.loginUser(loginUserDto);
    }


    @GetMapping("/search-user")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Search user", nickname = "searchUserOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseDto<UserSearchResponseDto> searchUser(@RequestParam String name, @RequestParam int page) {
        return userService.searchBusinessUser(name, page);
    }


    @GetMapping(value = "/get-userById/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    public ResponseEntity<UserDto> getUserById(
            @ApiParam(value = "Get user by ID.", required = true) @PathVariable("userId") int postId) {
        return userService.getUserByPostId(postId);
    }

    @PostMapping(value = "/liked-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Liked users", nickname = "likedUserOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity<User> likeUser(
            @ApiParam(value = "User data for create user") @RequestBody UserDto data) {
        logger.info("Controller "+data.toString());
        return userService.saveLikedUser(data);
    }
}
