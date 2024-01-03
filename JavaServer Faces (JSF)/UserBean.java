package com.example.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class UserBean {

    private String username;
    private String password;

    // Getters and setters for 'username' and 'password'

    public String login() {
        // Perform login logic here
        if ("user123".equals(username) && "password123".equals(password)) {
            return "success";
        } else {
            return "failure";
        }
    }
}
