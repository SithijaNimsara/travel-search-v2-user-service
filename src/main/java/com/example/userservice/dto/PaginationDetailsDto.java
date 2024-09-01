package com.example.userservice.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaginationDetailsDto {
    private long totalElements;
    private int pageNumber;
    private int pageSize;
}
