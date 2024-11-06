package com.ip.api.service;

import com.ip.api.domain.Customer;
import com.ip.api.domain.User;
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
                .user(user)  // user가 null일 수 있음에 유의
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        // CustomerResponse로 변환하여 반환
        return convertToDTO(savedCustomer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponse getCustomerById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(this::convertToDTO).orElse(null);
    }

    // 사업자등록번호 중복 확인 메서드
    public boolean isRegistrationNumberDuplicate(String registrationNumber) {
        return customerRepository.existsByRegistrationNumber(registrationNumber);
    }

    private CustomerResponse convertToDTO(Customer customer) {
        // user가 null일 때 userName과 userId 필드 처리
        String userName = customer.getUser() != null ? customer.getUser().getUserName() : "정보 없음";
        Long userId = customer.getUser() != null ? customer.getUser().getUserId() : null;

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
                userName,
                userId
        );
    }

    public List<CustomerResponse> searchCustomersByName(String customerName) {
        return customerRepository.findByCustomerNameContaining(customerName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean deleteCustomerById(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public CustomerResponse updateCustomer(Long id, User user, CustomerDTO customerRequest) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);

        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();

            if (customerRequest.getCustomerName() != null) {
                customer.setCustomerName(customerRequest.getCustomerName());
            }
            if (customerRequest.getCustomerPhone() != null) {
                customer.setCustomerPhone(customerRequest.getCustomerPhone());
            }
            if (customerRequest.getCustomerSdate() != null) {
                customer.setCustomerSdate(customerRequest.getCustomerSdate());
            }
            if (customerRequest.getCustomerStatus() != null) {
                customer.setCustomerStatus(customerRequest.getCustomerStatus());
            }
            if (customerRequest.getCustomerAddress() != null) {
                customer.setCustomerAddress(customerRequest.getCustomerAddress());
            }
            if (customerRequest.getCustomerAdddetail() != null) {
                customer.setCustomerAdddetail(customerRequest.getCustomerAdddetail());
            }
            if (customerRequest.getCustomerPersonName() != null) {
                customer.setCustomerPersonName(customerRequest.getCustomerPersonName());
            }
            if (customerRequest.getCustomerPersonPhone() != null) {
                customer.setCustomerPersonPhone(customerRequest.getCustomerPersonPhone());
            }
            if (customerRequest.getCustomerPersonEmail() != null) {
                customer.setCustomerPersonEmail(customerRequest.getCustomerPersonEmail());
            }
            if (customerRequest.getRegistrationNumber() != null) {
                customer.setRegistrationNumber(customerRequest.getRegistrationNumber());
            }
            if (customerRequest.getCustomerNote() != null) {
                customer.setCustomerNote(customerRequest.getCustomerNote());
            }

            customer.setUser(user); // user가 null일 가능성에 대비

            Customer updatedCustomer = customerRepository.save(customer);

            return convertToDTO(updatedCustomer);
        } else {
            return null;
        }
    }
}
