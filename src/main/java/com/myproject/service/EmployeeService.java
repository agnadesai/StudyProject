package com.myproject.service;

import com.myproject.domain.Employee;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    public boolean hasAccess(Employee employee) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String principalUsername = ((UserDetails) principal).getUsername();
            if(employee.getUsername().equals(principalUsername) || principalUsername.equals("admin")) {
                return true;
            }
        }
        return false;
    }

}
