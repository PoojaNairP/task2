package com.Aspire.tasktwo.Service;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.Aspire.tasktwo.Model.Employee;
import com.Aspire.tasktwo.Repository.EmployeeRepo;
//import com.Aspire.tasktwo.Service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private EmployeeRepo employeeRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    String dateTimeString;
     
        
        

    @Test
    public void testPostData_ValidEmployees() {
        // Given
        Employee validEmployee = new Employee();
        validEmployee.setEmployeeId(101);
        validEmployee.setName("John Doe");
        validEmployee.setDesignation("Associate");
        validEmployee.setDepartment("Sales");
        validEmployee.setEmail("john.doe@example.com");
        validEmployee.setMobile("1234567890");
        validEmployee.setLocation("Location");
        dateTimeString="2024-06-28T12:57:59";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        validEmployee.setDateOfJoining(dateTime);
        validEmployee.setManagerId(1);

        List<Employee> employees = Collections.singletonList(validEmployee);

        when(employeeRepo.existsById(101)).thenReturn(false);
        when(employeeRepo.existsByDepartmentAndDesignation("Sales", "Account Manager")).thenReturn(false);
        when(employeeRepo.findEmployeeByDesignationAndDepartment("Account Manager", "Sales")).thenReturn(Optional.empty());

        // When
        ResponseEntity<Map<String, String>> response = postService.postData(employees);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("employee_101");
        assertThat(response.getBody()).doesNotContainKey("error_101");
    }

    @Test
    public void testPostData_DuplicateEmployeeId() {
        // Given
        Employee duplicateEmployee = new Employee();
        duplicateEmployee.setEmployeeId(101);
        duplicateEmployee.setName("John Doe");
        duplicateEmployee.setDesignation("Associate");
        duplicateEmployee.setDepartment("sales");
        duplicateEmployee.setEmail("john.doe@example.com");
        duplicateEmployee.setMobile("1234567890");
        duplicateEmployee.setLocation("Location");
        dateTimeString="2024-06-28T12:57:59";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        duplicateEmployee.setDateOfJoining(dateTime);
        duplicateEmployee.setManagerId(1);

        List<Employee> employees = Collections.singletonList(duplicateEmployee);

        when(employeeRepo.existsById(101)).thenReturn(true);

        // When
        ResponseEntity<Map<String, String>> response = postService.postData(employees);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("error_101", "Employee ID 101 already exists.");
    }

    @Test
    public void testPostData_DepartmentAlreadyHasManager() {
        // Given
        Employee newEmployee = new Employee();
        newEmployee.setEmployeeId(103);
        newEmployee.setName("Mike Smith");
        newEmployee.setDesignation("Account Manager");
        newEmployee.setDepartment("Sales");
        newEmployee.setEmail("mike.smith@example.com");
        newEmployee.setMobile("1122334455");
        newEmployee.setLocation("Location");
        dateTimeString="2024-06-28T12:57:59";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        newEmployee.setDateOfJoining(dateTime);
        newEmployee.setManagerId(0); 
    
        List<Employee> employees = Collections.singletonList(newEmployee);

        when(employeeRepo.existsById(103)).thenReturn(false);
        when(employeeRepo.existsByDepartmentAndDesignation("Sales", "Account Manager")).thenReturn(true);

        // When
        ResponseEntity<Map<String, String>> response = postService.postData(employees);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("error_Sales", "Department Sales already has a manager.");
    }

    @Test
    public void testPostData_ManagerIdValidation() {
        // Given
        Employee newEmployee = new Employee();
        newEmployee.setEmployeeId(103);
        newEmployee.setName("Mike Smith");
        newEmployee.setDesignation("Associate");
        newEmployee.setDepartment("Sales");
        newEmployee.setEmail("mike.smith@example.com");
        newEmployee.setMobile("1122334455");
        newEmployee.setLocation("Location");
        dateTimeString="2024-06-28T12:57:59";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        newEmployee.setDateOfJoining(dateTime);
        newEmployee.setManagerId(2); 

        List<Employee> employees = Collections.singletonList(newEmployee);

        Employee existingManager = new Employee();
        existingManager.setEmployeeId(2);
        existingManager.setDesignation("Account Manager");
        existingManager.setDepartment("Sales");

        when(employeeRepo.existsById(103)).thenReturn(false);
        when(employeeRepo.existsByDepartmentAndDesignation("Sales", "Account Manager")).thenReturn(false);
        when(employeeRepo.findEmployeeByDesignationAndDepartment("Account Manager", "Sales")).thenReturn(Optional.of(existingManager));

        // When
        ResponseEntity<Map<String, String>> response = postService.postData(employees);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("employee_103", "Employee ID 103 is saved.");
    }

    @Test
    @DisplayName("xxxxxx")
    public void testPostData_ExceptionHandling() {
        // Given
        Employee validEmployee = new Employee();
        validEmployee.setEmployeeId(104);
        validEmployee.setName("Emily Davis");
        validEmployee.setDesignation("Associate");
        validEmployee.setDepartment("Sales");
        validEmployee.setEmail("emily.davis@example.com");
        validEmployee.setMobile("5566778899");
        validEmployee.setLocation("Location");
        dateTimeString="2024-06-28T12:57:59";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        validEmployee.setDateOfJoining(dateTime);

        List<Employee> employees = Collections.singletonList(validEmployee);

        when(employeeRepo.existsById(104)).thenReturn(false);
        when(employeeRepo.existsByDepartmentAndDesignation("Sales", "Account Manager")).thenReturn(false);
        when(employeeRepo.findEmployeeByDesignationAndDepartment("Account Manager", "Sales")).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Database error")).when(employeeRepo).saveAll(anyList());

        // When
        ResponseEntity<Map<String, String>> response = postService.postData(employees);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "Error adding data to database: Database error");
    }
}



















// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;
// import static org.assertj.core.api.Assertions.assertThat;

// //import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.ResponseEntity;

// import jakarta.validation.ConstraintViolation;
// import jakarta.validation.Validation;
// import jakarta.validation.Validator;
// import jakarta.validation.ValidatorFactory;

// @SpringBootTest
// public class PostServiceTest {

//     @Autowired
//     PostService postService;
//     @Autowired
//     EmployeeRepo employeeRepo;

//     private Validator validator;
    
//     @BeforeEach
//     public void setUp(){
//         ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
//         validator=factory.getValidator();
//     }

//     @Test
//     public void testPostData() {
//         // Given
//         Employee employee = new Employee();
//         employee.setEmployeeId(101);
//         employee.setName("test1");
//         employee.setDepartment("Sales");
//         employee.setDesignation("Associate");
//         employee.setEmail("test1@aspire.com");
//         employee.setLocation("Trivandrum");
//         String dateTimeString = "2022-06-28T12:57:59";
//         LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
//         //employee.setDateOfJoining(2022-06-28T12:57:59);
//         employee.setDateOfJoining(dateTime);
//         employee.setManagerId(1);


//         Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

//         // Assert that there are violations due to @NotNull constraints
//         assertThat(violations).isEmpty();

//         // When
//         ResponseEntity<Map<String, String>> response = postService.postData(List.of(employee));

//         // Then
//         assertThat(response.getStatusCode().value()).isEqualTo(200);
//         assertThat(employeeRepo.findById(101)).isPresent();
//     }

    
// }

// private Validator validator;

//     @BeforeEach
//     public void setUp() {
//         ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//         validator = factory.getValidator();
//     }

//     @Test
//     public void testPostData() {
//         Employee employee = new Employee();
//         employee.setEmployeeId(101); // Only setting employeeId

//         Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

//         // Assert that there are violations due to @NotNull constraints
//         assertThat(violations).isNotEmpty();
//     }