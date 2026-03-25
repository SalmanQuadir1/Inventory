package com.medicalstore.inventory.repository;

import com.medicalstore.inventory.entity.CustomerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, Long> {
    List<CustomerTransaction> findByCustomerIdOrderByTransactionDateDesc(Long customerId);
}
