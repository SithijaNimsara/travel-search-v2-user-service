package com.example.userservice.dto;

import com.example.userservice.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private int postId;

    private String caption;

    private Timestamp time;

    private byte[] image;;

    private User hotelId;

    private Set<User> postUsers = new HashSet<>();

}
