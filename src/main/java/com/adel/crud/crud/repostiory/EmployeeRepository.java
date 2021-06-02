package com.adel.crud.crud.repostiory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adel.crud.crud.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

	
}
