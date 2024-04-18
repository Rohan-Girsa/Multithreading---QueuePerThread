package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

	@Value("${WorkerThread}")
	private Integer workerThread;

	private ThreadPoolTaskExecutor taskExecutor;

	@Bean(name = "AsyncTaskExecutor")
	public ThreadPoolTaskExecutor asyncTaskExecutor() {
		taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(workerThread);
		taskExecutor.setMaxPoolSize(workerThread);
		taskExecutor.setThreadNamePrefix("MyAsyncThread-");
		return taskExecutor;
	}
}