package com.getxinfo.controller;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;

public class Listener {
	
    @KafkaListener(id = "foo", topics = "createUserTopic")
    public void listen1(GenericRecord avroRecord) {
    	System.out.println(avroRecord.get("username"));
    }


}
