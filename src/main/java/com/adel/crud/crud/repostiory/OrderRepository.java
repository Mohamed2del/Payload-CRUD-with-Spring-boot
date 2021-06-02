package com.adel.crud.crud.repostiory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adel.crud.crud.entity.Order;

public interface  OrderRepository extends JpaRepository<Order, Long> {

}
