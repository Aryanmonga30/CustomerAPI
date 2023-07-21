package com.api.customer.dto.request;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class AddRequestDto {
   private boolean enable;
   private int client;
   private String name;
   private String phoneNumber;
   private String customerCode;
   private String email;
   private int pageNumber;
   private int id;
}
