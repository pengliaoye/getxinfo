package com.getxinfo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.getxinfo.ConnectionSettings;

@Controller
public class GreetingController {
	
	private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);
		
	@Autowired
	private ConnectionSettings connectionSettings;
	
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
		logger.info(connectionSettings.getJdbc_url());
    	
        model.addAttribute("name", name);
        return "greeting";
    }

}
