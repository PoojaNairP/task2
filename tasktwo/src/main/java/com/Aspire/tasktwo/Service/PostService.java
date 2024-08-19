package com.Aspire.tasktwo.Service;

import com.Aspire.tasktwo.Model.Employee;
import com.Aspire.tasktwo.Repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    EmployeeRepo employeeRepo;

    @Transactional
    public ResponseEntity<Map<String, String>> postData(List<Employee> employees) {

        Map<String, String> errors = new HashMap<>();
        List<Employee> validEmployees = new ArrayList<>();

        for (Employee employee : employees) {

            List<String> validationErrors = employee.validate();
            if (!validationErrors.isEmpty()) {
                for (String error : validationErrors) {
                    errors.put("error_" + employee.getEmployeeId(), error);
                }
                continue;
            }
            // Check if employeeId already exists
            if (employeeRepo.existsById(employee.getEmployeeId())) {
                errors.put("error_" + employee.getEmployeeId(), "Employee ID " + employee.getEmployeeId() + " already exists.");
                continue; // Skip to the next employee
            }

            // Check if department already has a manager
            if ("Account Manager".equals(employee.getDesignation())) {
                if (employeeRepo.existsByDepartmentAndDesignation(employee.getDepartment(), "Account Manager")) {
                    errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " already has a manager.");
                    continue; // Skip to the next employee
                }
            }

            // Get the manager ID for the department
            Optional<Employee> optionalEmployee = employeeRepo.findEmployeeByDesignationAndDepartment("Account Manager", employee.getDepartment());
            if (optionalEmployee.isPresent()) {
                Integer employeeId = optionalEmployee.get().getEmployeeId();
                if (!employee.getManagerId().equals(employeeId)) {
                    // If the employee is not a manager, their managerId should match the existing manager
                    errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " have the manager ID "+employeeId);
                    continue;
                }
            }

// System.out.println("Manager employee ID for department " + employee.getDepartment() + " is: " + managerEmployeeId);
//             // Validate managerId
//             if (managerEmployeeId != null) {
//                 if (employee.getManagerId() == 0) {
//                     // If current employee is a manager, their managerId should be 0
//                     if (!"Manager".equals(employee.getDesignation())) {
//                         errors.put("error_" + employee.getEmployeeId(), "Manager should have managerId 0.");
//                         continue;
//                     }
//                 } else if (!employee.getManagerId().equals(managerEmployeeId)) {
//                     // If the employee is not a manager, their managerId should match the existing manager
//                     errors.put("error_" + employee.getDepartment(), "All employees in department " + employee.getDepartment() + " should have the same manager ID.");
//                     continue;
//                 }
//              }
             // else if (employee.getManagerId() != 0) {
            //     // If no manager exists for the department but employee has managerId set
            //     errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " does not have a manager.");
            //     continue;
            // }

            // Add valid employees to the list
            validEmployees.add(employee);
        }

        // if (!errors.isEmpty()) {
        //     return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        // }

        try {
            employeeRepo.saveAll(validEmployees);
            Map<String, String> successResult = new HashMap<>();
            //successResult.put("message", "Data added to database successfully");
            successResult.putAll(validEmployees.stream()
                .collect(Collectors.toMap(
                    emp -> "employee_" + emp.getEmployeeId(),
                    emp -> "Employee ID " + emp.getEmployeeId() + " is saved."
                )));
            if(errors.isEmpty())
                return new ResponseEntity<>(successResult, HttpStatus.OK);
            else{
                errors.putAll(successResult);
                return new ResponseEntity<>(errors,HttpStatus.OK);
            }
        } catch (Exception e) {
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("error", "Error adding data to database: " + e.getMessage());
            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}






// package com.Aspire.tasktwo.Service;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class PostService {

//     @Autowired
//     EmployeeRepo employeeRepo;

//     @Transactional
//     public ResponseEntity<Map<String, String>> postData(List<Employee> employees) {

//         Map<String, String> errors = new HashMap<>();
//         List<Employee> validEmployees = new ArrayList<>();
//         Map<String, Integer> departmentManagerMap = new HashMap<>();
//         Set<String> departmentsWithManagers = new HashSet<>();
//         Set<Integer> managerEmployeeIds = new HashSet<>();

//         // First pass: Validate existing IDs and department managers
//         for (Employee employee : employees) {
//             // Check if employeeId already exists
//             if (employeeRepo.existsById(employee.getEmployeeId())) {
//                 errors.put("error_" + employee.getEmployeeId(), "Employee ID " + employee.getEmployeeId() + " already exists.");
//                 continue; // Skip to the next employee
//             }

//             // Validate manager's data
//             if ("Manager".equals(employee.getDesignation())) {
//                 if (employee.getManagerId() != 0) {
//                     errors.put("error_" + employee.getEmployeeId(), "Manager must have managerId of 0.");
//                     continue;
//                 }

//                 if (departmentsWithManagers.contains(employee.getDepartment())) {
//                     errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " already has a manager.");
//                     continue;
//                 }

//                 // Record the manager's employeeId for the department
//                 departmentManagerMap.put(employee.getDepartment(), employee.getEmployeeId());
//                 departmentsWithManagers.add(employee.getDepartment());
//                 managerEmployeeIds.add(employee.getEmployeeId());
//             } else if (employee.getManagerId() == 0) {
//                 // Non-managers should not have managerId of 0
//                 errors.put("error_" + employee.getEmployeeId(), "Non-manager employees should not have managerId of 0.");
//                 continue;
//             }

//             // Add valid employees to the list
//             validEmployees.add(employee);
//         }

//         // Second pass: Validate managerId consistency within each department
//         for (Employee employee : validEmployees) {
//             if ("Manager".equals(employee.getDesignation())) {
//                 continue; // Skip the manager
//             }
//             Integer expectedManagerId = departmentManagerMap.get(employee.getDepartment());
//             if (expectedManagerId == null) {
//                 errors.put("error_" + employee.getDepartment(), "No manager found for department " + employee.getDepartment());
//                 break; // Exit the loop as we've detected a problem
//             }
//             if (!expectedManagerId.equals(employee.getManagerId())) {
//                 errors.put("error_" + employee.getDepartment(), "All employees in department " + employee.getDepartment() + " should have the manager ID of the department's manager.");
//                 break; // Exit the loop as we've detected an inconsistency
//             }
//         }

//         // Check if any duplicate manager entries exist
//         for (Employee employee : employees) {
//             if (employee.getDesignation().equals("Manager")) {
//                 if (managerEmployeeIds.contains(employee.getEmployeeId())) {
//                     errors.put("error_" + employee.getEmployeeId(), "Duplicate manager detected with ID " + employee.getEmployeeId() + " in department " + employee.getDepartment());
//                 }
//             }
//         }

//         if (!errors.isEmpty()) {
//             return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//         }

//         try {
//             employeeRepo.saveAll(validEmployees);
//             Map<String, String> successResult = new HashMap<>();
//             successResult.put("message", "Data added to database successfully");
//             successResult.putAll(validEmployees.stream()
//                 .collect(Collectors.toMap(
//                     emp -> "employee_" + emp.getEmployeeId(),
//                     emp -> "Employee ID " + emp.getEmployeeId() + " is saved."
//                 )));
//             return new ResponseEntity<>(successResult, HttpStatus.OK);
//         } catch (Exception e) {
//             Map<String, String> errorResult = new HashMap<>();
//             errorResult.put("error", "Error adding data to database: " + e.getMessage());
//             return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }
// }








// package com.Aspire.tasktwo.Service;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class PostService {

//     @Autowired
//     EmployeeRepo employeeRepo;

//     @Transactional
//     public ResponseEntity<Map<String, String>> postData(List<Employee> employees) {

//         Map<String, String> errors = new HashMap<>();
//         List<Employee> validEmployees = new ArrayList<>();
//         Map<String, Integer> departmentManagerMap = new HashMap<>();
//         Set<String> departmentsWithManagers = new HashSet<>();

//         // First pass: Validate existing IDs and department managers
//         for (Employee employee : employees) {
//             // Check if employeeId already exists
//             if (employeeRepo.existsById(employee.getEmployeeId())) {
//                 errors.put("error_" + employee.getEmployeeId(), "Employee ID " + employee.getEmployeeId() + " already exists.");
//                 continue; // Skip to the next employee
//             }

//             // Validate manager's data
//             if ("Manager".equals(employee.getDesignation())) {
//                 if (employee.getManagerId() != 0) {
//                     errors.put("error_" + employee.getEmployeeId(), "Manager must have managerId of 0.");
//                     continue;
//                 }

//                 // Check for existing manager in the same department
//                 if (departmentsWithManagers.contains(employee.getDepartment())) {
//                     errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " already has a manager.");
//                     continue;
//                 }

//                 // Set manager's employeeId as the managerId for the department
//                 departmentManagerMap.put(employee.getDepartment(), employee.getEmployeeId());
//                 departmentsWithManagers.add(employee.getDepartment());
//             }

//             // Add valid employees to the list
//             validEmployees.add(employee);
//         }

//         // Second pass: Validate managerId consistency within each department
//         for (Employee employee : validEmployees) {
//             if ("Manager".equals(employee.getDesignation())) {
//                 continue; // Skip the manager
//             }
//             Integer expectedManagerId = departmentManagerMap.get(employee.getDepartment());
//             if (expectedManagerId == null) {
//                 errors.put("error_" + employee.getDepartment(), "No manager found for department " + employee.getDepartment());
//                 break; // Exit the loop as we've detected a problem
//             }
//             if (!expectedManagerId.equals(employee.getManagerId())) {
//                 errors.put("error_" + employee.getDepartment(), "All employees in department " + employee.getDepartment() + " should have the manager ID of the department's manager.");
//                 break; // Exit the loop as we've detected an inconsistency
//             }
//         }

//         if (!errors.isEmpty()) {
//             return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//         }

//         try {
//             employeeRepo.saveAll(validEmployees);
//             Map<String, String> successResult = new HashMap<>();
//             successResult.put("message", "Data added to database successfully");
//             successResult.putAll(validEmployees.stream()
//                 .collect(Collectors.toMap(
//                     emp -> "employee_" + emp.getEmployeeId(),
//                     emp -> "Employee ID " + emp.getEmployeeId() + " is saved."
//                 )));
//             return new ResponseEntity<>(successResult, HttpStatus.OK);
//         } catch (Exception e) {
//             Map<String, String> errorResult = new HashMap<>();
//             errorResult.put("error", "Error adding data to database: " + e.getMessage());
//             return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }
// }















// package com.Aspire.tasktwo.Service;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// //import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class PostService {

//     @Autowired
//     EmployeeRepo employeeRepo;

//     @Transactional
//     public ResponseEntity<Map<String, String>> postData(List<Employee> employees) {

//         Map<String, String> errors = new HashMap<>();
//         List<Employee> validEmployees = new ArrayList<>();

//         for (Employee employee : employees) {
//             // Check if employeeId already exists
//             if (employeeRepo.existsById(employee.getEmployeeId())) {
//                 errors.put("error_" + employee.getEmployeeId(), "Employee ID " + employee.getEmployeeId() + " already exists.");
//                 continue; // Skip to the next employee
//             }

//             // Check if department already has a manager
//             if ("Manager".equals(employee.getDesignation())) {
//                 if (employeeRepo.existsByDepartmentAndDesignation(employee.getDepartment(), "Manager")) {
//                     errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " already has a manager.");
//                     continue; // Skip to the next employee
//                 }
//             }

//            // Optional<Integer> optionalEmployeeId = employeeRepo.findEmployeeIdByDesignationAndDepartment("Manager", employee.getDepartment());
    
//            Integer employeeId = employeeRepo.findEmployeeIdByDesignationAndDepartment("Manager", employee.getDepartment());
//             // Handle the Optional result
//                 System.out.println("manager id of "+employee.getDepartment()+" is : "+employee.getManagerId()+" whereas the employeeid got is "+employeeId);
//                 if(employee.getManagerId()!=employeeId && employeeId!=null){
//                     errors.put("error_" + employee.getDepartment(), "Department " + employee.getDepartment() + " has a manager.");
//                     continue;
//                 }

//             // Add valid employees to the list
//             validEmployees.add(employee);
//         }

//         if (!errors.isEmpty()) {
//             return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//         }

//         try {
//             employeeRepo.saveAll(validEmployees);
//             Map<String, String> successResult = new HashMap<>();
//             successResult.put("message", "Data added to database successfully");
//             successResult.putAll(validEmployees.stream()
//                 .collect(Collectors.toMap(
//                     emp -> "employee_" + emp.getEmployeeId(),
//                     emp -> "Employee ID " + emp.getEmployeeId() + " is saved."
//                 )));
//             return new ResponseEntity<>(successResult, HttpStatus.OK);
//         } catch (Exception e) {
//             Map<String, String> errorResult = new HashMap<>();
//             errorResult.put("error", "Error adding data to database: " + e.getMessage());
//             return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }
// }















// package com.Aspire.tasktwo.Service;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// @Service
// public class PostService {

//     @Autowired
//     private EmployeeRepo employeeRepo;

//     public ResponseEntity<Map<String, String>> postData(List<Employee> employees) {

//         Map<String, String> responseMessages = new HashMap<>();
//         List<Employee> validEmployees = new ArrayList<>();

//         // Check for existing employee IDs and collect valid employees
//         for (Employee employee : employees) {
//             if (employeeRepo.existsByEmployeeId(employee.getEmployeeId())) {
//                 // Add an error message for existing IDs
//                 responseMessages.put("error_" + employee.getEmployeeId(), "Employee ID " + employee.getEmployeeId() + " already exists.");
//             } else {
//                 // Collect valid employees for saving
//                 validEmployees.add(employee);
//             }
//         }

//         // Attempt to save valid employees
//         if (!validEmployees.isEmpty()) {
//             try {
//                 // Save each valid employee and add a success message
//                 for (Employee employee : validEmployees) {
//                     employeeRepo.save(employee);
//                     responseMessages.put("success_" + employee.getEmployeeId(), "Employee ID " + employee.getEmployeeId() + " saved successfully.");
//                 }
//             } catch (Exception e) {
//                 responseMessages.put("error_saving", "Error saving valid employees: " + e.getMessage());
//             }
//         }

//         // Determine response status
//         HttpStatus status = responseMessages.containsKey("error_") ? HttpStatus.BAD_REQUEST : HttpStatus.OK;

//         return new ResponseEntity<>(responseMessages, status);
//     }
// }






// package com.Aspire.tasktwo.Service;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;
// import com.mongodb.MongoWriteException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// @Service
// public class PostService {

//     @Autowired
//     private EmployeeRepo employeeRepo;

//     public ResponseEntity<Map<String, String>> postData(List<Employee> employees) {
//         // Check for employeeId and managerId conflicts
//         for (Employee employee : employees) {
//             if (employee.getEmployeeId() == employee.getManagerId()) {
//                 Map<String, String> error = new HashMap<>();
//                 error.put("error", "Employee and manager id can't be the same.");
//                 return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//             }
//         }

//         // Check for existing employeeIds
//         for (Employee employee : employees) {
//             if (employeeRepo.existsById(employee.getEmployeeId())) {
//                 Map<String, String> error = new HashMap<>();
//                 error.put("error", "Duplicate employeeId found: " + employee.getEmployeeId());
//                 return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//             }
//         }

//         try {
//             employeeRepo.saveAll(employees);
//             Map<String, String> postResult = new HashMap<>();
//             postResult.put("message", "Data added to database successfully");
//             return new ResponseEntity<>(postResult, HttpStatus.OK);
//         } catch (MongoWriteException e) {
//             // Handle duplicate key error specifically
//             Map<String, String> errorResult = new HashMap<>();
//             errorResult.put("error", "Duplicate key error: " + e.getMessage());
//             return new ResponseEntity<>(errorResult, HttpStatus.CONFLICT);
//         } catch (Exception e) {
//             // Handle other exceptions
//             Map<String, String> errorResult = new HashMap<>();
//             errorResult.put("error", "Error adding data to database: " + e.getMessage());
//             return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }
// }
