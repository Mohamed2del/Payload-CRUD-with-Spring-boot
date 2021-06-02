package com.adel.crud.crud.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adel.crud.crud.entity.Employee;
import com.adel.crud.crud.exception.EmployeeNotFoundException;
import com.adel.crud.crud.model.EmployeeModelAssembler;
import com.adel.crud.crud.repostiory.EmployeeRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.util.stream.Collectors;


@RestController
public class EmployeeController {

  private final EmployeeRepository repository;
  private final EmployeeModelAssembler assembler;

  EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

    this.repository = repository;
    this.assembler = assembler;
  }


  // Aggregate root
  // tag::get-aggregate-root[]
  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/employees")
public
  CollectionModel<EntityModel<Employee>> all() {

	  List<EntityModel<Employee>> employees = repository.findAll().stream() //
		      .map(assembler::toModel) //
		      .collect(Collectors.toList());

		  return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());

  }
  // end::get-aggregate-root[]

  @PostMapping("/employees")
  public ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

	  EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

	  return ResponseEntity //
	      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
	      .body(entityModel);
	}
  // Single item
  
  @GetMapping("/employees/{id}")
public
  EntityModel<Employee> one(@PathVariable int id) {

    Employee employee = repository.findById(id) //
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    return assembler.toModel(employee);
  }

  @PutMapping("/employees/{id}")
  public ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable int id) {

    Employee updatedEmployee = repository.findById(id) //
        .map(employee -> {
          employee.setName(newEmployee.getName());
          employee.setRole(newEmployee.getRole());
          return repository.save(employee);
        }) //
        .orElseGet(() -> {
          newEmployee.setId(id);
          return repository.save(newEmployee);
        });

    EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

    return ResponseEntity //
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
        .body(entityModel);
  }

  @DeleteMapping("/employees/{id}")
  public ResponseEntity<?> deleteEmployee(@PathVariable int id) {

    repository.deleteById(id);

    return ResponseEntity.noContent().build();
  }
}