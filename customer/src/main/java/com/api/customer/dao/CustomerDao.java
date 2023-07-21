package com.api.customer.dao;

import com.api.customer.model.CustomerModel;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    public List<CustomerModel> getCustomers(int pageNo);
    public CustomerModel getCustomerById(int id);
    public CustomerModel addCustomer(CustomerModel customer);
    public CustomerModel getCustomerByCode(String customerCode);
    public List<CustomerModel> getCustomersByClientId(int client);
}
