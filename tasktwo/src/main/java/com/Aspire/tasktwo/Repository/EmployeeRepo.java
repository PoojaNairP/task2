package com.Aspire.tasktwo.Repository;

//import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Optional;
import java.util.Optional;

//import java.util.List;

import com.Aspire.tasktwo.Model.Employee;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.repository.query.Param;

public interface EmployeeRepo extends MongoRepository<Employee,Integer> {

    // Find all employee IDs
    @Query(value = "{}", fields = "{ 'employeeId' : 1 }")
    List<Integer> findAllEmployeeIds();

    // Find the manager for each department
    @Query(value = "{ 'designation': 'Manager' }", fields = "{ 'department' : 1, 'employeeId': 1 }")
    List<Employee> findDepartmentManagers();

    boolean existsByDepartmentAndDesignation(String department, String string);

    List<Employee> findEmployeesByManagerId(Integer managerId);

    List<Employee> findByManagerId(Integer managerId);

    // Find an employee by designation and department
    @Query(value = "{'designation': ?0, 'department': ?1}")
    Optional<Employee> findEmployeeByDesignationAndDepartment(String designation, String department);

    // Find employees by experience cutoff date and managerId
    @Query(value = "{'dateOfJoining': { $lte: ?0 }, 'managerId': ?1}")
    List<Employee> findByExperienceAndManagerId(LocalDateTime experienceCutoffDate, int managerId);

    // Find employees by experience cutoff date
    @Query(value = "{'dateOfJoining': { $lte: ?0 }}")
    List<Employee> findByExperience(LocalDateTime experienceCutoffDate);

    Optional<Employee> findByEmployeeId(int employeeId);

    //Optional<Integer> findEmployeeIdByDesignationAndDepartment(String string, String department);

    //List<Employee> findByManagerId(Integer managerId);

    

    
}
