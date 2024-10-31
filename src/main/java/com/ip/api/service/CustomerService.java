package com.ip.api.service;

import com.ip.api.domain.Customer;
import com.ip.api.domain.User;
import com.ip.api.dto.customer.CustomerRequest.CustomerDTO;
import com.ip.api.dto.customer.CustomerResponse;
import com.ip.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerResponse createCustomer(User user, CustomerDTO customerRequest) {
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
                .user(user)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        // CustomerResponse로 변환하여 반환
        return new CustomerResponse(
                savedCustomer.getCustomerId(),
                savedCustomer.getCustomerName(),
                savedCustomer.getCustomerPhone(),
                savedCustomer.getCustomerSdate(),
                savedCustomer.getCustomerStatus().toString(),
                savedCustomer.getCustomerAddress(),
                savedCustomer.getCustomerAdddetail(),
                savedCustomer.getCustomerPersonName(),
                savedCustomer.getCustomerPersonPhone(),
                savedCustomer.getCustomerPersonEmail(),
                savedCustomer.getRegistrationNumber(),
                savedCustomer.getCustomerNote(),
                user.getUserName() // User의 이름만 포함
        );
    }
}
