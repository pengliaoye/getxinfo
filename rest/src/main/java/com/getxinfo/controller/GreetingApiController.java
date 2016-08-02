package com.getxinfo.controller;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.getxinfo.Greeting;
import com.getxinfo.PinbiConfig;
import com.getxinfo.PinbiConfigRepository;


@RestController
@RequestMapping("/api")
public class GreetingApiController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	private PinbiConfigRepository pinbiConfigRepository;

	@Autowired
	public GreetingApiController(PinbiConfigRepository pinbiConfigRepository) {
		this.pinbiConfigRepository = pinbiConfigRepository;
	}

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@GetMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@RequestMapping("/pinbiconfig")
	public Iterable<PinbiConfig> pinbiconfig() {
		return pinbiConfigRepository.findAll();
	}

	@PostMapping("/pinbiconfig")
	public void savePinbiConfig(@RequestBody @Valid PinbiConfig config) {
		pinbiConfigRepository.save(config);
	}

}
