package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.CustomerDto;
import com.medicalstore.inventory.dto.CustomerTransactionDto;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto updateCustomer(Long id, CustomerDto customerDto);
    CustomerDto getCustomerById(Long id);
    void deleteCustomer(Long id);
    
    Page<CustomerDto> getPaginatedCustomers(int pageNo, int pageSize, String sortField, String sortDir, String keyword);
    List<CustomerDto> getAllCustomersForStore();
    
    // Ledger Operations
    void recordPayment(Long customerId, BigDecimal amount, String notes);
    void recordCreditSale(Long customerId, BigDecimal amount, String reference);
    
    List<CustomerTransactionDto> getCustomerLedger(Long customerId);
}
