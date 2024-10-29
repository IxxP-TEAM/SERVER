package com.ip.api.service;

import com.ip.api.domain.Customer;
import com.ip.api.domain.User;
import com.ip.api.dto.customer.CustomerRequest.CustomerDTO;
import com.ip.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(User user, CustomerDTO customerRequest) {
        Customer customer = Customer.builder()
                .customerName(customerRequest.getCustomerName())
                .customerPhone(customerRequest.getCustomerPhone())
                .customerSdate(customerRequest.getCustomerSdate())
                .customerStatus(customerRequest.getCustomerStatus())
                .customerAddress(customerRequest.getCustomerAddress())
                .customerAdddetail(customerRequest.getCustomerAdddetail())
                .customerPersonName(customerRequest.getCustomerPersonName())
                .customerPersonPhone(customerRequest.getCustomerPersonPhone())
                .customerPersonEmail(customerRequest.getCustomerPersonEmail())
                .registrationNumber(customerRequest.getRegistrationNumber())
                .customerNote(customerRequest.getCustomerNote())
                .user(user) // @AuthUser로 전달된 인증된 사용자 설정
                .build();

        return customerRepository.save(customer);
    }
}
