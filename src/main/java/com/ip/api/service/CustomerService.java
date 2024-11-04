package com.ip.api.service;

import com.ip.api.domain.Customer;
import com.ip.api.domain.User;
import com.ip.api.dto.customer.CustomerRequest;
import com.ip.api.dto.customer.CustomerRequest.CustomerDTO;
import com.ip.api.dto.customer.CustomerResponse;
import com.ip.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                user.getUserName(),
                user.getUserId()// User의 이름만 포함
        );
    }

    // 모든 고객 정보 리스트
    public List<CustomerResponse> getAllCustomers(){
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //ID로 특정 고객사 정보 조회
    public CustomerResponse getCustomerById(Long id){
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(this::convertToDTO).orElse(null);
    }


    private CustomerResponse convertToDTO(Customer customer){
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getCustomerPhone(),
                customer.getCustomerSdate(),
                customer.getCustomerStatus().toString(),
                customer.getCustomerAddress(),
                customer.getCustomerAdddetail(),
                customer.getCustomerPersonName(),
                customer.getCustomerPersonPhone(),
                customer.getCustomerPersonEmail(),
                customer.getRegistrationNumber(),
                customer.getCustomerNote(),
                customer.getUser().getUserName(), // User의 이름
                customer.getUser().getUserId()    // User의 ID
        );
    }

    // 이름으로 고객사 검색
    public List<CustomerResponse> searchCustomersByName(String customerName) {
        return customerRepository.findByCustomerNameContaining(customerName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //고객사 삭제
    public boolean deleteCustomerById(Long id){
        if(customerRepository.existsById(id)){
            customerRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }

    //고객사 수정
    public CustomerResponse updateCustomer(Long id, User user, CustomerDTO customerRequest){
        Optional<Customer> existingCustomer = customerRepository.findById(id);

        if(existingCustomer.isPresent()){
            Customer customer = existingCustomer.get();
            customer.setCustomerName(customerRequest.getCustomerName());
            customer.setCustomerPhone(customerRequest.getCustomerPhone());
            customer.setCustomerSdate(customerRequest.getCustomerSdate());
            customer.setCustomerStatus(customerRequest.getCustomerStatus());
            customer.setCustomerAddress(customerRequest.getCustomerAddress());
            customer.setCustomerAdddetail(customerRequest.getCustomerAdddetail());
            customer.setCustomerPersonName(customerRequest.getCustomerPersonName());
            customer.setCustomerPersonPhone(customerRequest.getCustomerPersonPhone());
            customer.setCustomerPersonEmail(customerRequest.getCustomerPersonEmail());
            customer.setRegistrationNumber(customerRequest.getRegistrationNumber());
            customer.setCustomerNote(customerRequest.getCustomerNote());
            customer.setUser(user); // 담당자 정보 업데이트

            Customer updatedCustomer = customerRepository.save(customer);

            return new CustomerResponse(
                    updatedCustomer.getCustomerId(),
                    updatedCustomer.getCustomerName(),
                    updatedCustomer.getCustomerPhone(),
                    updatedCustomer.getCustomerSdate(),
                    updatedCustomer.getCustomerStatus().toString(),
                    updatedCustomer.getCustomerAddress(),
                    updatedCustomer.getCustomerAdddetail(),
                    updatedCustomer.getCustomerPersonName(),
                    updatedCustomer.getCustomerPersonPhone(),
                    updatedCustomer.getCustomerPersonEmail(),
                    updatedCustomer.getRegistrationNumber(),
                    updatedCustomer.getCustomerNote(),
                    user.getUserName(), // User의 이름
                    user.getUserId()    // User의 ID
            );
        }else {
            return null;
        }
    }

}
