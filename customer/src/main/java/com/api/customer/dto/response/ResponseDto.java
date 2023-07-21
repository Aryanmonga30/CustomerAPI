package com.api.customer.dto.response;
import com.api.customer.model.CustomerModel;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResponseDto {
    private Boolean status;
    private Object data;
    private String message;
}
