package com.example.userservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="UserNoImageRequestDto")
public class UserNoImageRequestDto {
    @ApiModelProperty(value = "User's name", dataType = "String")
    private String name;

    @ApiModelProperty(value = "User's email", dataType = "String")
    private String email;

    @ApiModelProperty(value = "User's password", dataType = "String")
    private String password;

    @ApiModelProperty(value = "User's address", dataType = "String")
    private String address;

    @ApiModelProperty(value = "User's state", dataType = "String")
    private String state;

    @ApiModelProperty(value = "User's country", dataType = "String")
    private String country;

    @ApiModelProperty(value = "User's role", dataType = "String")
    private String role;
}
