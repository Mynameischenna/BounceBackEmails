package com.example.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailApplication {

	public static void main(String[] args) {
		System.out.println("Starting Spring Boot Email Application...");

		// Run the monitoring in a separate thread to avoid blocking the main thread
		Thread monitorThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				BounceBackMonitor.monitorBounceBacks(); // Periodically check for bounce-back emails
				try {
					Thread.sleep(60000); // Check every 60 seconds
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore the interruption status
				}
			}
		});

		monitorThread.start();

		// Starts the Spring Boot application context
		SpringApplication.run(EmailApplication.class, args);
		System.out.println("Spring Boot Email Application is running...");

		// Ensures the program ends when Spring Boot shuts down
		Runtime.getRuntime().addShutdownHook(new Thread(() -> monitorThread.interrupt()));
	}
}
