package com.supermarket.billing.repository;

import com.supermarket.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, String> {
}
