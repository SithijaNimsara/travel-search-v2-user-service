package com.example.userservice.error;


import com.example.userservice.dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class HttpExceptionResponse {
    public final List<ErrorDto> errors;
}
