package com.api.customer.cache;

import com.api.customer.model.CustomerModel;
import org.apache.kafka.common.protocol.types.Field;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerCache {
    private static final String CUSTOMER_ID_TO_CUSTOMER_MAP="CUSTOMER_ID_TO_CUSTOMER_MAP";
    private static final String CLIENT_ID_TO_CUSTOMER_MAP="CLIENT_ID_TO_CUSTOMER_MAP";
    private static final int CUSTOMERS_PER_PAGE=5;
    @Autowired
    RedissonClient redissonClient;
    public CustomerModel getCustomerByCustomerId(int id){
        RMap<Integer,CustomerModel> map=redissonClient.getMap(CUSTOMER_ID_TO_CUSTOMER_MAP);
        return map.getOrDefault(id,null);
    }
    public List<CustomerModel> getCustomerByClientId(int client){
        RMap<Integer,List<CustomerModel>> map=redissonClient.getMap(CLIENT_ID_TO_CUSTOMER_MAP);
        return map.getOrDefault(client,null);
    }
    public void putCustomerToMaps(CustomerModel customerModel) {
        RMap<Integer, CustomerModel> customerIdMap = redissonClient.getMap(CUSTOMER_ID_TO_CUSTOMER_MAP);
        if (customerIdMap != null) {
            customerIdMap.put(customerModel.getId(), customerModel);
        }
        RMap<Integer, List<CustomerModel>> clientMap = redissonClient.getMap(CLIENT_ID_TO_CUSTOMER_MAP);
        if (clientMap != null) {
            List<CustomerModel> customerModelList = clientMap.get(customerModel.getClient());
            if (customerModelList == null) {
                customerModelList = new ArrayList<>();
            } else {
                for (CustomerModel customerModel3 : customerModelList) {
                    if (customerModel3.getId() == customerModel.getId()) {
                        customerModelList.remove(customerModel);
                        return;
                    }
                }
            }
            customerModelList.add(customerModel);
            clientMap.put(customerModel.getClient(), customerModelList);
        }

    }


}
