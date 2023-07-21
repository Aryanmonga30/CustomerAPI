package com.api.customer.util;

import com.api.customer.dto.request.AddRequestDto;
import com.api.customer.dto.response.ValidResponseDto;
import com.api.customer.model.CustomerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Pattern;
@Component
public class Validate {

    public static Timestamp setDate(){
        Calendar calendar = Calendar.getInstance();
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static ValidResponseDto isValid(AddRequestDto addRequestDto) {
        ValidResponseDto validResponseDto= new ValidResponseDto();
        if (addRequestDto.getName() == null || addRequestDto.getPhoneNumber() == null || addRequestDto.getEmail() == null || addRequestDto.getClient() == 0 || addRequestDto.getCustomerCode() == null) {
            validResponseDto.setValidData(false);
            validResponseDto.setMessage("Please specify all the required fields");
            return validResponseDto;
        }
        else{
            if (Validate.isValidName(addRequestDto.getName()) && Validate.isValidEmail(addRequestDto.getEmail()) && Validate.isValidCustomerCode(addRequestDto.getCustomerCode()) && Validate.isValidPhoneNumber(addRequestDto.getPhoneNumber())) {
                validResponseDto.setValidData(true);
                return validResponseDto;
            }
            else {
                if (!Validate.isValidCustomerCode(addRequestDto.getCustomerCode())) {
                    validResponseDto.setMessage("Specify the correct customer Code upto length 10");
                    validResponseDto.setValidData(false);
                }
                else if (!Validate.isValidEmail(addRequestDto.getEmail())) {
                    validResponseDto.setMessage("Specify the correct email");
                    validResponseDto.setValidData(false);
                }
                else if (!Validate.isValidPhoneNumber(addRequestDto.getPhoneNumber())) {
                    validResponseDto.setMessage("Specify the correct phone Number");
                    validResponseDto.setValidData(false);
                }
                else if (!Validate.isValidName(addRequestDto.getName())) {
                    validResponseDto.setMessage("Specify the name upto length 30");
                    validResponseDto.setValidData(false);
                }
                return validResponseDto;
            }

        }
    }
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }
    public static boolean isValidCustomerCode(String customerCode){
        if(customerCode.length()>=10){
            return false;
        }
        return true;
    }
    public static boolean isValidName(String name){
        if(name.length()>=20){
            return false;
        }
        return true;
    }


}
