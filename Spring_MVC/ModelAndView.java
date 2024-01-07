package com.example.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DataController {

    // Assuming fetchData() is a method that retrieves data
    private String fetchData() {
        // Mock data for demonstration purposes
        return "Sample Data";
    }

    @GetMapping("/displayData")
    public ModelAndView displayData() {
        // logic for handling requests and returning ModelAndView
        ModelAndView modelAndView = new ModelAndView("dataView");
        modelAndView.addObject("data", fetchData());
        return modelAndView;
    }
  
}
