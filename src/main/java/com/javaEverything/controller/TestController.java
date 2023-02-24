package com.javaEverything.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linmeng
 * @date 2023/2/24 16:55
 */
@RestController
@RequestMapping("/c")
public class TestController {

	@GetMapping("test")
	public String test(){

		return "dkfghdskjf";
	}
}
