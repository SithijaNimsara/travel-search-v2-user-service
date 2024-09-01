package com.example.userservice.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseDto<T> {
    private List<T> data;
    private PaginationDetailsDto pageable;
}
