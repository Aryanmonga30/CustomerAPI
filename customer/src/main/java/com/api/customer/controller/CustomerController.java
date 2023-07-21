package com.api.customer.controller;

import com.api.customer.dto.request.AddRequestDto;
import com.api.customer.dto.response.ResponseDto;
import com.api.customer.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/customers/v1")

public class CustomerController {
    @Autowired
    CustomerService customerService;
    @GetMapping("/get-customers")
    public ResponseEntity<ResponseDto> getCustomers(@RequestBody AddRequestDto addRequestDto){
        return customerService.getCustomers(addRequestDto.getPageNumber());
    }
    @GetMapping("/get-customer-by-id")
    public ResponseEntity<ResponseDto> getCustomerById(@RequestBody AddRequestDto addRequestDto){
        return customerService.getCustomerById(addRequestDto.getId());
    }
    @PostMapping("/add-customer-by-kafka")
    public ResponseEntity<ResponseDto> addCustomersByKafka(@RequestBody AddRequestDto addRequestDto){
        ResponseEntity<ResponseDto> response= customerService.addCustomersByKafka(addRequestDto);
        return response;
    }
    @GetMapping("/get-customers-by-client-id")
    public ResponseEntity<ResponseDto> getCustomersByClientId(@RequestBody AddRequestDto addRequestDto){
        return customerService.getCustomersByClientId(addRequestDto.getClient());
    }
    @PostMapping("/save-or-update-customers")
    public ResponseEntity<ResponseDto> saveOrUpdateCustomer(@RequestBody AddRequestDto addRequestDto){
        return customerService.saveOrUpdateCustomer(addRequestDto);
    }
    @GetMapping("/load-redis")
    public ResponseEntity<String> loadRedis() throws JsonProcessingException {

        return customerService.loadRedis();
    }

}
