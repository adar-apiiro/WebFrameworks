package com.example;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

@Namespace("/api")
public class ApiAction {

    private String message;

    @Action(value = "hello", results = { @Result(name = "success", type = "json") })
    public String hello() {
        message = "Hello, API!";
        return "success";
    }

    public String getMessage() {
        return message;
    }
}
