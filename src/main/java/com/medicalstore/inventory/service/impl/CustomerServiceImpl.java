package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.CustomerDto;
import com.medicalstore.inventory.dto.CustomerTransactionDto;
import com.medicalstore.inventory.entity.Customer;
import com.medicalstore.inventory.entity.CustomerTransaction;
import com.medicalstore.inventory.entity.User;
import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.exception.ResourceNotFoundException;
import com.medicalstore.inventory.repository.CustomerRepository;
import com.medicalstore.inventory.repository.CustomerTransactionRepository;
import com.medicalstore.inventory.repository.UserRepository;
import com.medicalstore.inventory.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private java.util.Set<Warehouse> getCurrentUserWarehouses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(User::getWarehouses)
                .orElse(java.util.Collections.emptySet());
    }

    private Warehouse getPrimaryWarehouse() {
        return getCurrentUserWarehouses().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setTotalBalance(BigDecimal.ZERO);
        
        Warehouse store = getPrimaryWarehouse();
        customer.setStore(store);

        Customer saved = customerRepository.save(customer);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public CustomerDto updateCustomer(Long id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        
        Customer updated = customerRepository.save(customer);
        return mapToDto(updated);
    }

    @Override
    @SuppressWarnings("null")
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return mapToDto(customer);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (customer.getTotalBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("Cannot delete customer with non-zero outstanding balance.");
        }
        customerRepository.delete(customer);
    }

    @Override
    public List<CustomerDto> getAllCustomersForStore() {
        java.util.Set<Long> warehouseIds = getCurrentUserWarehouses().stream().map(Warehouse::getId).collect(Collectors.toSet());
        List<Customer> allCustomers = customerRepository.findAll();
        if (!warehouseIds.isEmpty()) {
            allCustomers = allCustomers.stream().filter(c -> c.getStore() != null && warehouseIds.contains(c.getStore().getId())).collect(Collectors.toList());
        }
        return allCustomers.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public Page<CustomerDto> getPaginatedCustomers(int pageNo, int pageSize, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        java.util.Set<Long> warehouseIds = getCurrentUserWarehouses().stream().map(Warehouse::getId).collect(Collectors.toSet());
        
        List<Customer> allCustomers = customerRepository.findAll(sort);
        if (!warehouseIds.isEmpty()) {
            allCustomers = allCustomers.stream().filter(c -> c.getStore() != null && warehouseIds.contains(c.getStore().getId())).collect(Collectors.toList());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            allCustomers = allCustomers.stream().filter(c -> 
                c.getName().toLowerCase().contains(kw) || 
                (c.getPhone() != null && c.getPhone().toLowerCase().contains(kw))
            ).collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCustomers.size());
        
        List<CustomerDto> dtoList;
        if (start <= end) {
            dtoList = allCustomers.subList(start, end).stream().map(this::mapToDto).collect(Collectors.toList());
        } else {
            dtoList = java.util.Collections.emptyList();
        }
        
        return new PageImpl<>(dtoList, pageable, allCustomers.size());
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void recordPayment(Long customerId, BigDecimal amount, String notes) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        customer.setTotalBalance(customer.getTotalBalance().subtract(amount));
        customerRepository.save(customer);

        CustomerTransaction tx = CustomerTransaction.builder()
                .customer(customer)
                .amount(amount)
                .transactionType("PAYMENT")
                .transactionDate(LocalDateTime.now())
                .notes(notes)
                .build();
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void recordCreditSale(Long customerId, BigDecimal amount, String reference) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sale amount must be greater than zero");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        customer.setTotalBalance(customer.getTotalBalance().add(amount));
        customerRepository.save(customer);

        CustomerTransaction tx = CustomerTransaction.builder()
                .customer(customer)
                .amount(amount)
                .transactionType("CREDIT_SALE")
                .reference(reference)
                .transactionDate(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);
    }

    @Override
    public List<CustomerTransactionDto> getCustomerLedger(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByTransactionDateDesc(customerId)
                .stream().map(this::mapTransactionToDto)
                .collect(Collectors.toList());
    }

    private CustomerDto mapToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setTotalBalance(customer.getTotalBalance());
        dto.setCreatedAt(customer.getCreatedAt());
        if (customer.getStore() != null) {
            dto.setStoreId(customer.getStore().getId());
            dto.setStoreName(customer.getStore().getName());
        }
        return dto;
    }

    private CustomerTransactionDto mapTransactionToDto(CustomerTransaction tx) {
        CustomerTransactionDto dto = new CustomerTransactionDto();
        dto.setId(tx.getId());
        dto.setCustomerId(tx.getCustomer().getId());
        dto.setCustomerName(tx.getCustomer().getName());
        dto.setAmount(tx.getAmount());
        dto.setTransactionType(tx.getTransactionType());
        dto.setReference(tx.getReference());
        dto.setTransactionDate(tx.getTransactionDate());
        dto.setNotes(tx.getNotes());
        return dto;
    }
}
