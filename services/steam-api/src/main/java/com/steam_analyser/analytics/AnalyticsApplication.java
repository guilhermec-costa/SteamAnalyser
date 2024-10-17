package com.steam_analyser.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.steam_analyser.analytics.concurrents.WebApiRunnable;

@SpringBootApplication
public class AnalyticsApplication {

	public static void main(String[] args) {
    Thread webApiThread = new Thread(new WebApiRunnable());
    webApiThread.start();
		SpringApplication.run(AnalyticsApplication.class, args);
	}
}
