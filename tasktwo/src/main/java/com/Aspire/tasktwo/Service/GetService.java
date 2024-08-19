package com.Aspire.tasktwo.Service;

import com.Aspire.tasktwo.Model.Employee;
import com.Aspire.tasktwo.Repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.HashMap;
//import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// @Service
// public class GetService {

//     @Autowired
//     private EmployeeRepo employeeRepo;

//     public ResponseEntity<?> getEmployeeDetails(int managerId, int yearsOfExperience) {
//         LocalDateTime currentDateTime = LocalDateTime.now();
//         List<Employee> employees = employeeRepo.findAll();
//         Map<String, String> errors = new HashMap<>();

//         // Calculate the cutoff datetime for experience filtering
//         LocalDateTime experienceCutoffDateTime = yearsOfExperience != -1 
//                 ? currentDateTime.minusYears(yearsOfExperience) 
//                 : null;

//         // List<Employee> filteredEmployees = employees.stream()
//         //     .filter(employee -> {
//         //         // Calculate experience using compareTo
//         //         boolean matchesExperience = experienceCutoffDateTime == null 
//         //                 || employee.getDateOfJoining().compareTo(experienceCutoffDateTime) <= 0;
//         //         boolean matchesManager = managerId == 0 || employee.getManagerId() == managerId;
//         //         return matchesExperience && matchesManager;
//         //     })
//         //     .collect(Collectors.toList());
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

//         List<Employee> filteredEmployees = employees.stream()
//             .filter(employee -> {
//                 // Convert the dateOfJoining string to OffsetDateTime
//                 OffsetDateTime employeeDateOfJoining;
//                 try {
//                     employeeDateOfJoining = OffsetDateTime.parse(employee.getDateOfJoining(), formatter);
//                 } catch (Exception e) {
//                     // Handle parsing errors, if necessary
//                     employeeDateOfJoining = null;
//                 }

//                 // Convert OffsetDateTime to LocalDateTime for comparison
//                 LocalDateTime employeeDateOfJoiningLocal = (employeeDateOfJoining != null) 
//                         ? employeeDateOfJoining.toLocalDateTime() 
//                         : null;

//                 // Calculate experience using compareTo
//                 boolean matchesExperience = experienceCutoffDateTime == null 
//                         || (employeeDateOfJoiningLocal != null && employeeDateOfJoiningLocal.compareTo(experienceCutoffDateTime) <= 0);
//                 boolean matchesManager = managerId == 0 || (employee.getManagerId() != null && employee.getManagerId() == managerId);
//                 return matchesExperience && matchesManager;
//             })
//             .collect(Collectors.toList());

//             if(filteredEmployees.isEmpty()){
//                 errors.put("error","No employee exists with the given credentials");
//                 return new ResponseEntity<>(errors, HttpStatus.OK);
//             }

//             Optional<Employee> accountManagerOptional = employeeRepo.findById(managerId);
//             Integer accountManagerId= accountManagerOptional.map(Employee::getEmployeeId).orElse(0);
//             String accountManagerName = accountManagerOptional.map(Employee::getName).orElse("Unknown");
//             String accountManagerDepartment=accountManagerOptional.map(Employee::getDepartment).orElse("Unknown");


//             Map<String, Object> response = new LinkedHashMap<>();
//             response.put("message", "successfully fetched");

//             Map<String, Object> details = new LinkedHashMap<>();
//             details.put("accountManager", accountManagerName);
//             details.put("department", accountManagerDepartment);
//             details.put("id", accountManagerId);
//             details.put("employeeList", filteredEmployees.stream().map(emp -> {
//                 Map<String, Object> employeeDetails = new LinkedHashMap<>();
//                 employeeDetails.put("name", emp.getName());
//                 employeeDetails.put("id", emp.getEmployeeId());
//                 employeeDetails.put("designation", emp.getDesignation());
//                 employeeDetails.put("department", emp.getDepartment());
//                 employeeDetails.put("email", emp.getEmail());
//                 employeeDetails.put("mobile", emp.getMobile());
//                 employeeDetails.put("location", emp.getLocation());
//                 employeeDetails.put("dateOfJoining", emp.getDateOfJoining());
//                 employeeDetails.put("createdTime", emp.getDateOfJoining()); 
//                 employeeDetails.put("updatedTime", emp.getDateOfJoining()); 
//                 return employeeDetails;
//             }).collect(Collectors.toList()));

//         response.put("details", List.of(details));

//         // Map the list of employees to a response format
//         // Map<String, Object> response = Map.of(
//         //     "message", "successfully fetched",
//         //     "details", List.of(
//         //         Map.of(
//         //             "accountManager", accountManagerName,
//         //             "department", accountManagerDepartment, 
//         //             "id",accountManagerId ,               
//         //             "employeeList", filteredEmployees.stream().map(emp -> Map.of(
//         //                 "name", emp.getName(),
//         //                 "id", emp.getEmployeeId(),
//         //                 "designation", emp.getDesignation(),
//         //                 "department", emp.getDepartment(),
//         //                 "email", emp.getEmail(),
//         //                 "mobile", emp.getMobile(),
//         //                 "location", emp.getLocation(),
//         //                 "dateOfJoining", emp.getDateOfJoining().format(formatter),
//         //                 "createdTime", emp.getDateOfJoining().format(formatter), // Replace with actual created time if available
//         //                 "updatedTime", emp.getDateOfJoining().format(formatter)  // Replace with actual updated time if available
//         //             )).collect(Collectors.toList())
//         //         )
//         //     )
//         // );

//         return new ResponseEntity<>(response, HttpStatus.OK);
//     }
// }












// package com.Aspire.tasktwo.Service;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

@Service
public class GetService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public ResponseEntity<?> getEmployeeDetails(int managerId, int yearsOfExperience) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Employee> employees = employeeRepo.findAll();
        Map<String, String> errors = new HashMap<>();

        // Calculate the cutoff datetime for experience filtering
        LocalDateTime experienceCutoffDateTime = yearsOfExperience != -1 
                ? currentDateTime.minusYears(yearsOfExperience) 
                : null;

        List<Employee> filteredEmployees = employees.stream()
            .filter(employee -> {
                // Calculate experience using compareTo
                boolean matchesExperience = experienceCutoffDateTime == null 
                        || employee.getDateOfJoining().compareTo(experienceCutoffDateTime) <= 0;
                boolean matchesManager = managerId == 0 || employee.getManagerId() == managerId;
                return matchesExperience && matchesManager;
            })
            .collect(Collectors.toList());
            if(filteredEmployees.isEmpty()){
                errors.put("error","No employee exists with the given credentials");
                return new ResponseEntity<>(errors, HttpStatus.OK);
            }

            Optional<Employee> accountManagerOptional = employeeRepo.findById(managerId);
            Integer accountManagerId= accountManagerOptional.map(Employee::getEmployeeId).orElse(0);
            String accountManagerName = accountManagerOptional.map(Employee::getName).orElse("Unknown");
            String accountManagerDepartment=accountManagerOptional.map(Employee::getDepartment).orElse("Unknown");


            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "successfully fetched");

            Map<String, Object> details = new LinkedHashMap<>();
            details.put("accountManager", accountManagerName);
            details.put("department", accountManagerDepartment);
            details.put("id", accountManagerId);
            details.put("employeeList", filteredEmployees.stream().map(emp -> {
                Map<String, Object> employeeDetails = new LinkedHashMap<>();
                employeeDetails.put("name", emp.getName());
                employeeDetails.put("id", emp.getEmployeeId());
                employeeDetails.put("designation", emp.getDesignation());
                employeeDetails.put("department", emp.getDepartment());
                employeeDetails.put("email", emp.getEmail());
                employeeDetails.put("mobile", emp.getMobile());
                employeeDetails.put("location", emp.getLocation());
                employeeDetails.put("dateOfJoining", emp.getDateOfJoining());
                employeeDetails.put("createdTime", emp.getDateOfJoining()); 
                employeeDetails.put("updatedTime", emp.getDateOfJoining()); 
                return employeeDetails;
            }).collect(Collectors.toList()));

        response.put("details", List.of(details));

        // Map the list of employees to a response format
        // Map<String, Object> response = Map.of(
        //     "employees", filteredEmployees.stream().map(emp -> Map.of(
        //         "employeeId", emp.getEmployeeId(),
        //         "name", emp.getName(),
        //         "designation", emp.getDesignation(),
        //         "department", emp.getDepartment(),
        //         "location", emp.getLocation(),
        //         "dateOfJoining", emp.getDateOfJoining()
        //     )).collect(Collectors.toList())
        // );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
