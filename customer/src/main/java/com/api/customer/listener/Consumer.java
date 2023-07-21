package com.api.customer.listener;
import com.api.customer.dao.CustomerDaoImplementation;
import com.api.customer.model.CustomerModel;
import com.api.customer.service.CustomerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    @Autowired
    CustomerService customerService;
    @Value("${spring.kafka.test.name}")

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerDaoImplementation.class);
    @KafkaListener(topics = "${spring.kafka.test.name}" , groupId = "groupId",containerFactory = "userKafkaListenerFactory")
    public void consumeMessage(CustomerModel message) {
        logger.info("Consumed Message");
        customerService.saveAddedCustomer(message);
    }
}

