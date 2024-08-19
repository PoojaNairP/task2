package com.Aspire.tasktwo.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.stream.Collectors;

import com.Aspire.tasktwo.Model.Employee;
import com.Aspire.tasktwo.Repository.EmployeeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PutService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public ResponseEntity<Map<String, String>> updateEmployeeManagers(List<Map<String, Integer>> employeeManagerPairs) {
        Map<String, String> responseMessages = new HashMap<>();
        Map<String, String> failedUpdates = new HashMap<>();

        for (Map<String, Integer> pair : employeeManagerPairs) {
            Integer employeeId = pair.get("employeeId");
            Integer newManagerId = pair.get("managerId");

            if (employeeId == null || newManagerId == null) {
                failedUpdates.put("error_" + employeeId, "Employee ID or Manager ID is missing");
                continue;
            }

            // Fetch the employee
            Employee employee = employeeRepo.findById(employeeId).orElse(null);
            if (employee == null) {
                failedUpdates.put("error_" + employeeId, "Employee not found");
                continue;
            }

            // Fetch current manager
            Employee currentManager = employeeRepo.findById(employee.getManagerId()).orElse(null);
            if (currentManager == null) {
                failedUpdates.put("error_" + employeeId, "Current manager not found");
                continue;
            }

            // Fetch new manager
            Employee newManager = employeeRepo.findById(newManagerId).orElse(null);
            if (newManager == null) {
                failedUpdates.put("error_" + employeeId, "New manager not found");
                continue;
            }

            if(currentManager.getEmployeeId()==newManager.getEmployeeId()){
                failedUpdates.put("error_" + employeeId, "New manager is same as the current manager");
                continue;
            }

            // Check if employee is in the current manager's list
            List<Employee> employeesManagedByCurrent = employeeRepo.findByManagerId(employee.getManagerId());

            System.out.println("Employees managed by " + currentManager.getName() + ":");
                   for (Employee emp : employeesManagedByCurrent) {
            System.out.println(emp);
         }

            if (!employeesManagedByCurrent.contains(employee)) {
                failedUpdates.put("error_" + employeeId, "Employee is not in the current manager's list");
                continue;
            }

            // Remove employee from current manager's list
            employeesManagedByCurrent.remove(employee);
            employeeRepo.saveAll(employeesManagedByCurrent);

            // Update employee's managerId and department
            employee.setManagerId(newManagerId);
            employee.setDepartment(newManager.getDepartment());
            employeeRepo.save(employee);

            // Add employee to new manager's list
            List<Employee> employeesManagedByNew = employeeRepo.findByManagerId(newManagerId);
            employeesManagedByNew.add(employee);
            employeeRepo.saveAll(employeesManagedByNew);

            // Success message for this employee
            String message = String.format("%s's manager has been successfully changed from %s to %s.",
                    employee.getName(), currentManager.getName(), newManager.getName());
            responseMessages.put("employee_" + employeeId, message);
        }

        // Convert failedUpdates to Map<String, String>
        responseMessages.putAll(failedUpdates);

        if (!failedUpdates.isEmpty()) {
            return new ResponseEntity<>(responseMessages, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(responseMessages, HttpStatus.OK);
    }
}




// package com.Aspire.tasktwo.Service;

// import java.util.List;
// import java.util.Map;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// @Service
// public class PutService {

//     @Autowired
//     private EmployeeRepo employeeRepo;

//     public ResponseEntity<Map<String, String>> updateEmployeeManager(int employeeId, int newManagerId) {

//         // Fetch the employee
//         Employee employee = employeeRepo.findById(employeeId).orElse(null);
//         if (employee == null) {
//             return new ResponseEntity<>(Map.of("error", "Employee not found"), HttpStatus.NOT_FOUND);
//         }

//         // Fetch current manager
//         Employee currentManager = employeeRepo.findById(employee.getManagerId()).orElse(null);
//         if (currentManager == null) {
//             return new ResponseEntity<>(Map.of("error", "Current manager not found"), HttpStatus.NOT_FOUND);
//         }

//         // Fetch new manager
//         Employee newManager = employeeRepo.findById(newManagerId).orElse(null);
//         if (newManager == null) {
//             return new ResponseEntity<>(Map.of("error", "New manager not found"), HttpStatus.NOT_FOUND);
//         }

//         // Check if employee is in the current manager's list
//         List<Employee> employeesManagedByCurrent = employeeRepo.findByManagerId(employee.getManagerId());

//         System.out.println("Employees managed by " + currentManager.getName() + ":");
//         for (Employee emp : employeesManagedByCurrent) {
//             System.out.println(emp);
//         }

//         if (!employeesManagedByCurrent.contains(employee)) {
//             return new ResponseEntity<>(Map.of("error", "Employee is not in the current manager's list"), HttpStatus.BAD_REQUEST);
//         }

//         // Remove employee from current manager's list
//         employeesManagedByCurrent.remove(employee);
//         employeeRepo.saveAll(employeesManagedByCurrent);

//         // Update employee's managerId and department
//         employee.setManagerId(newManagerId);
//         employee.setDepartment(newManager.getDepartment());
//         employeeRepo.save(employee);

//         // Add employee to new manager's list
//         List<Employee> employeesManagedByNew = employeeRepo.findByManagerId(newManagerId);
//         employeesManagedByNew.add(employee);
//         employeeRepo.saveAll(employeesManagedByNew);

//         // Respond with success message
//         String message = String.format("%s's manager has been successfully changed from %s to %s.",
//                 employee.getName(), currentManager.getName(), newManager.getName());
//         return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
//     }
// }




















// package com.Aspire.tasktwo.Service;

// //import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// //import java.util.Optional;

// import com.Aspire.tasktwo.Model.Employee;
// import com.Aspire.tasktwo.Repository.EmployeeRepo;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// @Service
// public class PutService {

//     @Autowired
//     private EmployeeRepo employeeRepo;

//     public ResponseEntity<Map<String, String>> updateEmployeeManager(int employeeId,int newManagerId) {

//         // Fetch the employee
//         Employee employee = employeeRepo.findById(employeeId).orElse(null);
//         if (employee == null) {
//             return new ResponseEntity<>(Map.of("error", "Employee not found"), HttpStatus.NOT_FOUND);
//         }

//         // Fetch current manager
//         Employee currentManager = employeeRepo.findById(employee.getManagerId()).orElse(null);
//         if (currentManager == null) {
//             return new ResponseEntity<>(Map.of("error", "Current manager not found"), HttpStatus.NOT_FOUND);
//         }

//         // Fetch new manager
//         Employee newManager = employeeRepo.findById(newManagerId).orElse(null);
//         if (newManager == null) {
//             return new ResponseEntity<>(Map.of("error", "New manager not found"), HttpStatus.NOT_FOUND);
//         }

//         // Check if employee is in the current manager's list
//         List<Employee> employeesManagedByCurrent = employeeRepo.findByManagerId(employee.getManagerId());

//         System.out.println("Employees managed by " + currentManager.getName() + ":");
//             for (Employee emp : employeesManagedByCurrent) {
//                 System.out.println(emp);
//             }



//         if (!employeesManagedByCurrent.contains(employee)) {
//             return new ResponseEntity<>(Map.of("error", "Employee is not in the current manager's list"), HttpStatus.BAD_REQUEST);
//         }

//         // Remove employee from current manager's list and add to new manager's list
//         employee.setManagerId(newManagerId);
//         employeeRepo.save(employee);

//         // Respond with success message
//         String message = String.format("%s's manager has been successfully changed from %s to %s.",
//                 employee.getName(), currentManager.getName(), newManager.getName());
//         return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
//     }
// }