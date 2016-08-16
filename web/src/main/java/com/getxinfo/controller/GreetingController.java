package com.getxinfo.controller;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.getxinfo.ConnectionSettings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GreetingController {

	@Autowired
	private ConnectionSettings connectionSettings;

	@Autowired
	private Environment environment;

	@Autowired
	private KafkaTemplate<String, GenericRecord> template;

	@RequestMapping("/greeting")
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		log.info(connectionSettings.getJdbc_url());

		log.info(environment.getProperty("info.description"));

		String userSchema = "{\"type\":\"record\"," + "\"name\":\"user\","
				+ "\"fields\":[{\"name\":\"username\",\"type\":\"string\"},{\"name\":\"password\",\"type\":\"string\"}]}";
		Schema.Parser parser = new Schema.Parser();
		Schema schema = parser.parse(userSchema);
		GenericRecord avroRecord = new GenericData.Record(schema);
		avroRecord.put("username", "tet111");
		avroRecord.put("password", "123");

		template.send("createUserTopic", "tet11", avroRecord);
		model.addAttribute("name", name);
		return "greeting";
	}

}
