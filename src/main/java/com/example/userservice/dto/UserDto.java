package com.example.userservice.dto;

import com.example.userservice.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int userId;

    private String name;

    private String email;

    private String password;

    private String address;

    private String state;

    private String country;

    private String role;

    private byte[] image;

    private Set<PostDto> userPosts = new HashSet<>();

}
