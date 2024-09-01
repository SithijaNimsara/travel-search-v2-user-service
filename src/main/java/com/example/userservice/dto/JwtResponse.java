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
@ApiModel(value="LoginUserDto")
public class JwtResponse {

    @ApiModelProperty(value = "User's id", dataType = "int")
    private int userId;

    @ApiModelProperty(value = "User's name", dataType = "String")
    private String jwt;

    @ApiModelProperty(value = "User's name", dataType = "String")
    private String name;

    @ApiModelProperty(value = "User's role", dataType = "String")
    private String role;

}
