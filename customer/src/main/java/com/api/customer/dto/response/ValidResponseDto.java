package com.api.customer.dto.response;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ValidResponseDto {
    private String message;
    private boolean validData;
}
