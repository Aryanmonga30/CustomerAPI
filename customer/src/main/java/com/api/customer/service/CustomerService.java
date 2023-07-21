package com.api.customer.service;

import com.api.customer.cache.CustomerCache;
import com.api.customer.dao.CustomerDaoImplementation;
import com.api.customer.dto.request.AddRequestDto;
import com.api.customer.dto.response.ResponseDto;
import com.api.customer.dto.response.ValidResponseDto;
import com.api.customer.model.CustomerModel;
import com.api.customer.util.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    CustomerDaoImplementation customerDaoImplementation;
    @Autowired
    KafkaTemplate<String,CustomerModel> kafkaTemplate;
    @Value("${spring.kafka.test.name}")
    String topicName;
    @Autowired
    CustomerCache customerCache;
    public static final Logger logger = LoggerFactory.getLogger(CustomerDaoImplementation.class);
    public ResponseEntity<ResponseDto> getCustomers(int pageNo) {
        ResponseDto customerReturnListDto= new ResponseDto();
        // This function provides us all the customers and it uses pagination as well.
       try{
           if(pageNo<=0){
               customerReturnListDto.setMessage("Invalid Input");
               customerReturnListDto.setStatus(false);
               customerReturnListDto.setData(null);
               return new ResponseEntity<>(customerReturnListDto,HttpStatus.BAD_GATEWAY);
           }
           else {
               List<CustomerModel> values = customerDaoImplementation.getCustomers(pageNo);
               if(values!=null && values.size()>0){
                   customerReturnListDto.setMessage("Found all the Customers in the table");
                   customerReturnListDto.setStatus(true);
                   customerReturnListDto.setData(values);
                   return new ResponseEntity<>(customerReturnListDto, HttpStatus.ACCEPTED);
               }
               else{
                   customerReturnListDto.setStatus(false);
                   customerReturnListDto.setData(null);
                   customerReturnListDto.setMessage("Empty Record");
                   return new ResponseEntity<>(customerReturnListDto,HttpStatus.BAD_GATEWAY);
               }

           }
       }
       catch(Exception e){
           customerReturnListDto.setStatus(false);
           customerReturnListDto.setMessage("Values could not be found");
           customerReturnListDto.setData(null);
           logger.error("Values could not be found",e);
           return new ResponseEntity<>(customerReturnListDto,HttpStatus.BAD_GATEWAY);
       }
    }
    public ResponseEntity<ResponseDto> getCustomerById(int id) {
        // This function is used to fetch the customer of a particular customer id.
        ResponseDto responseDto= new ResponseDto();
        CustomerModel customerModel=customerCache.getCustomerByCustomerId(id);
        try {
            if (customerModel != null) {
                responseDto.setData(customerModel);
                responseDto.setStatus(true);
                responseDto.setMessage("Success" + " from cache");
            } else {
                customerModel = customerDaoImplementation.getCustomerById(id);
                if (customerModel == null) {
                    responseDto.setMessage("No such id exists");
                    responseDto.setStatus(false);
                    responseDto.setData(null);
                    logger.error("No such Id exists");
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }
                responseDto.setData(customerModel);
                responseDto.setStatus(true);
                responseDto.setMessage("Success" + " from database");
                customerCache.putCustomerToMaps(customerModel);
            }
            return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            responseDto.setStatus(false);
            responseDto.setMessage("Data could not be found."+ e);
            responseDto.setData(null);
            return new ResponseEntity<>(responseDto,HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<ResponseDto> addCustomersByKafka(AddRequestDto addRequestDto) {
        ResponseDto responseDto= new ResponseDto();
        try {
            CustomerModel existingCustomer = customerDaoImplementation.getCustomerByCode(addRequestDto.getCustomerCode());
            if (existingCustomer != null) {
                ValidResponseDto isValid = Validate.isValid(addRequestDto);
                if (isValid.isValidData()) {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setName(addRequestDto.getName());
                    customerModel.setEnable(addRequestDto.isEnable());
                    customerModel.setPhoneNumber(addRequestDto.getPhoneNumber());
                    customerModel.setEmail(addRequestDto.getEmail());
                    customerModel.setId(existingCustomer.getId());
                    customerModel.setCreateDate(Validate.setDate());
                    customerModel.setCustomerCode(addRequestDto.getCustomerCode());
                    customerModel.setClient(addRequestDto.getClient());
                    customerModel.setLastModifiedDate(Validate.setDate());
                    kafkaTemplate.send(topicName, customerModel);
                    responseDto.setStatus(true);
                    responseDto.setData(customerModel);
                    responseDto.setMessage("Successfully Published customer");
                    return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
                } else {
                    responseDto.setMessage(isValid.getMessage());
                    responseDto.setStatus(isValid.isValidData());
                    responseDto.setData(null);
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }
            } else {
                String message = null;
                ValidResponseDto isValid = Validate.isValid(addRequestDto);
                if (isValid.isValidData()) {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setCustomerCode(addRequestDto.getCustomerCode());
                    customerModel.setName(addRequestDto.getName());
                    customerModel.setEnable(addRequestDto.isEnable());
                    customerModel.setClient(addRequestDto.getClient());
                    customerModel.setPhoneNumber(addRequestDto.getPhoneNumber());
                    customerModel.setEmail(addRequestDto.getEmail());
                    customerModel.setCreateDate(Validate.setDate());
                    customerModel.setLastModifiedDate(Validate.setDate());
                    kafkaTemplate.send(topicName, customerModel);
                    responseDto.setStatus(true);
                    responseDto.setData(customerModel);
                    responseDto.setMessage("Customer published Successfully");
                } else {
                    responseDto.setStatus(false);
                    responseDto.setMessage(isValid.getMessage());
                    responseDto.setData(null);
                }
                return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
            }
        }
        catch (Exception e){
            logger.error(e.getMessage());
            responseDto.setStatus(false);
            responseDto.setMessage("Customer can't be added, an exception encountered"+e);
            responseDto.setData(null);
            return new ResponseEntity<>(responseDto,HttpStatus.BAD_GATEWAY);
        }
    }
    public void saveAddedCustomer(CustomerModel customerModel){
        try{
            customerDaoImplementation.addCustomer(customerModel);
            customerCache.putCustomerToMaps(customerModel);
        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public ResponseEntity<ResponseDto> getCustomersByClientId(int client) {
        ResponseDto responseDto = new ResponseDto();
        try {
            List<CustomerModel> customerModel = customerCache.getCustomerByClientId(client);
            if (customerModel != null && customerModel.size() > 0) {
                responseDto.setData(customerModel);
                responseDto.setStatus(true);
                responseDto.setMessage("Success from cache");
                return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
            } else {
                customerModel = customerDaoImplementation.getCustomersByClientId(client);
                if (customerModel == null || customerModel.size() == 0) {
                    responseDto.setMessage("No such id exists");
                    responseDto.setStatus(false);
                    responseDto.setData(null);
                    logger.error("No such Id exists");
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }

                // Cache the data before returning the response
                for (int i = 0; i < customerModel.size(); i++) {
                    customerCache.putCustomerToMaps(customerModel.get(i));
                }

                responseDto.setData(customerModel);
                responseDto.setStatus(true);
                responseDto.setMessage("Success from database");
                return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            responseDto.setData(null);
            responseDto.setStatus(false);
            responseDto.setMessage("An unknown exception encountered" + e);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
        }
    }



    public ResponseEntity<ResponseDto> saveOrUpdateCustomer(AddRequestDto addRequestDto) {
        ResponseDto responseDto = new ResponseDto();
        try {
            CustomerModel existingCustomer = customerDaoImplementation.getCustomerByCode(addRequestDto.getCustomerCode());
            if (existingCustomer != null) {
                ValidResponseDto isValid = Validate.isValid(addRequestDto);
                if (isValid.isValidData()) {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setName(addRequestDto.getName());
                    customerModel.setEnable(addRequestDto.isEnable());
                    customerModel.setPhoneNumber(addRequestDto.getPhoneNumber());
                    customerModel.setEmail(addRequestDto.getEmail());
                    customerModel.setId(existingCustomer.getId());
                    customerModel.setCreateDate(Validate.setDate());
                    customerModel.setCustomerCode(addRequestDto.getCustomerCode());
                    customerModel.setClient(addRequestDto.getClient());
                    customerModel.setLastModifiedDate(Validate.setDate());
                    CustomerModel resultantCustomerModel = customerDaoImplementation.addCustomer(customerModel);
                    customerCache.putCustomerToMaps(resultantCustomerModel);
                    responseDto.setStatus(true);
                    responseDto.setData(resultantCustomerModel);
                    responseDto.setMessage("Successfully updated customer");
                    return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
                } else {
                    responseDto.setMessage(isValid.getMessage());
                    responseDto.setStatus(isValid.isValidData());
                    responseDto.setData(null);
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }
            } else {
                String message = null;
                ValidResponseDto isValid = Validate.isValid(addRequestDto);
                if (isValid.isValidData()) {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setCustomerCode(addRequestDto.getCustomerCode());
                    customerModel.setName(addRequestDto.getName());
                    customerModel.setEnable(addRequestDto.isEnable());
                    customerModel.setClient(addRequestDto.getClient());
                    customerModel.setPhoneNumber(addRequestDto.getPhoneNumber());
                    customerModel.setEmail(addRequestDto.getEmail());
                    customerModel.setCreateDate(Validate.setDate());
                    customerModel.setLastModifiedDate(Validate.setDate());
                    CustomerModel resultantCustomerModel = customerDaoImplementation.addCustomer(customerModel);
                    customerCache.putCustomerToMaps(resultantCustomerModel);
                    responseDto.setStatus(true);
                    responseDto.setData(resultantCustomerModel);
                    responseDto.setMessage("Successfully added customer");
                    return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
                } else {
                    responseDto.setMessage(isValid.getMessage());
                    responseDto.setStatus(isValid.isValidData());
                    responseDto.setData(null);
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }
            }
        }
        catch (Exception e){
            logger.error(e.getMessage());
            responseDto.setData(null);
            responseDto.setStatus(false);
            responseDto.setMessage("An exception occured"+e);
            return new ResponseEntity<>(responseDto,HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<String> loadRedis() {
        List<CustomerModel> data = new ArrayList<>();
        int pageNo = 1;
        int pageSize = 5;
        try {
            do {
                List<CustomerModel> values = customerDaoImplementation.getCustomers(pageNo);
                // Retrieve tickets for the specified page and size
                data.addAll(values);
                if (values.size() != pageSize)
                    break;
                pageNo += 1;
            }
            while (true);// Retrieve tickets for the specified page and size
            for (CustomerModel customerModel : data) {
                customerCache.putCustomerToMaps(customerModel);
            }
            return ResponseEntity.ok("Data Loaded Successfully");
        } catch (Exception e) {
            // Handle exceptions and set appropriate values in the responseDto
            logger.error(e.getMessage());
            // Return the responseDto
            return ResponseEntity.badRequest().body("Data Not Loaded Successfully");
        }

    }
}
