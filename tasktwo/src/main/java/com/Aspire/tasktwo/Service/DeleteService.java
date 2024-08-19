package com.Aspire.tasktwo.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.Aspire.tasktwo.Model.Employee;
import com.Aspire.tasktwo.Repository.EmployeeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DeleteService {

    EmployeeRepo employeeRepo;

    @Autowired
    public DeleteService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public ResponseEntity<Map<String, Object>> deleteEmployee(int employeeId) {
        // Check if the employee exists
        Optional<Employee> employeeOptional = employeeRepo.findByEmployeeId(employeeId);
        if (employeeOptional.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Employee not found"), HttpStatus.NOT_FOUND);
        }

        // Check if there are subordinates
        List<Employee> subordinates = employeeRepo.findByManagerId(employeeId);
        if (!subordinates.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Cannot delete employee with subordinates"), HttpStatus.BAD_REQUEST);
        }

        // Delete the employee
        employeeRepo.deleteById(employeeId);

        // Return success message
        return new ResponseEntity<>(Map.of("message", "Successfully deleted employee with ID " + employeeId), HttpStatus.OK);
    }
    
}
