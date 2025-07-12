package vn.tuantrung.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuantrung.laptopshop.domain.OrderDetail;

@Repository

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
}
