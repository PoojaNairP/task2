package com.Aspire.tasktwo.Controller;

import java.util.List;
import java.util.Map;

import com.Aspire.tasktwo.Model.Employee;
import com.Aspire.tasktwo.Service.DeleteService;
import com.Aspire.tasktwo.Service.GetService;
import com.Aspire.tasktwo.Service.PostService;
import com.Aspire.tasktwo.Service.PutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class MainController {

    @Autowired
    private final PostService postService;
    private final PutService putService;
    private final GetService getService;
    private final DeleteService deleteService;

    public MainController(PostService postService,PutService putService, GetService getService, DeleteService deleteService){
        this.postService=postService;
        this.putService=putService;
        this.getService=getService;
        this.deleteService=deleteService;
    }
    

    @PostMapping("addData")
    public ResponseEntity<Map<String,String>> addData(@RequestBody @Valid List<Employee> employees) {
        return postService.postData(employees);
    }

    // @PutMapping("/update-manager")
    // public ResponseEntity<Map<String, String>> updateEmployeeManager(@RequestBody Map<String, Integer> requestBody) {
    //     int employeeId = requestBody.get("employeeId");
    //     int newManagerId = requestBody.get("managerId");
    //     return putService.updateEmployeeManagers(employeeId, newManagerId);
    // }

    @PutMapping("/update-managers")
    public ResponseEntity<Map<String, String>> updateEmployeeManagers(@RequestBody List<Map<String, Integer>> requestBody) {
        // Pass the list of employee-manager pairs to the service
        return putService.updateEmployeeManagers(requestBody);
    }

    @GetMapping("/get-employees")
    public ResponseEntity<?> getEmployeeDetails(@RequestParam(defaultValue = "0")int managerId, @RequestParam(defaultValue = "-1")int yearsOfExperience) {
        // Pass the list of employee-manager pairs to the service
        return getService.getEmployeeDetails(managerId,yearsOfExperience);
    }

    @DeleteMapping("/deleteEmployee")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@RequestParam int employeeId) {
        return deleteService.deleteEmployee(employeeId);
    }


    
}
