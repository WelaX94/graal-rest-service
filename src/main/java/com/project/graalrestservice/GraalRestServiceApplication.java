package com.project.graalrestservice;

import org.graalvm.polyglot.Context;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraalRestServiceApplication {

	public static void main(String[] args) {
		boolean jsDetected = false;
		Context context = Context.newBuilder().build();
		for(String s: context.getEngine().getLanguages().keySet()) {
			if (s.equals("js")) {
				jsDetected = true;
				break;
			}
		}
		context.close();
		if (jsDetected) {
			SpringApplication.run(GraalRestServiceApplication.class, args);
		}
		else {
			System.out.println("JS Engine not found. The program is closing");
		}
	}

}
