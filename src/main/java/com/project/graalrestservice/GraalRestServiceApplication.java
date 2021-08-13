package com.project.graalrestservice;

import org.graalvm.polyglot.Context;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class for containing main method
 */
@SpringBootApplication
public class GraalRestServiceApplication {

	/**
	 * Main method. It also checks for JS Engine. If it is missing, the service does not start.
	 */
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
