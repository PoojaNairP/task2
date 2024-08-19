package com.Aspire.tasktwo.Service;

import com.Aspire.tasktwo.Repository.EmployeeRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PutServiceTest {

    @InjectMocks
    private PutService putService;

    @Mock
    private EmployeeRepo employeeRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    String dateTimeString;

    @Test
    public void updateDetails_ValidEmloyee(){
        
    }
    
}
