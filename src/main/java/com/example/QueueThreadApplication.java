package com.example;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import com.sun.management.OperatingSystemMXBean;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.config.AsyncConfig;
import com.example.dto.CallFile;
import com.example.entities.TblClient;
import com.example.service.CallPublisher;
import com.example.service.impl.PopQueue;
import com.example.service.impl.PushQueue;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableAsync
@Slf4j
public class QueueThreadApplication {
	@Autowired
	private AsyncConfig asyncConfig;
	@Autowired
	private CallPublisher callPublisher;
	private List<TblClient> tblClientList;
	private ConcurrentHashMap<String, TblClient> clientQueue;
	private ConcurrentHashMap<String, BlockingQueue<CallFile>> queues;

	public static void main(String[] args) {
		SpringApplication.run(QueueThreadApplication.class, args);
	}

	@PostConstruct
	public void init() {
		tblClientList = callPublisher.getAllClient();
		clientQueue = new ConcurrentHashMap<>();
		queues = new ConcurrentHashMap<>();
		for (TblClient t : tblClientList) {
			clientQueue.put(t.getTemplateId().toString(), t);
			BlockingQueue<CallFile> queue = new LinkedBlockingQueue<>();
			queues.put(t.getTemplateId().toString(), queue);
		}
		int size = tblClientList.size();
		if (size > 0 && size != asyncConfig.asyncTaskExecutor().getCorePoolSize()) {
			((ThreadPoolTaskExecutor) asyncConfig.asyncTaskExecutor()).setCorePoolSize(size);
			((ThreadPoolTaskExecutor) asyncConfig.asyncTaskExecutor()).setMaxPoolSize(size);
		}
		ThreadPoolExecutor executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		PushQueue pushQueue = new PushQueue(queues, callPublisher);
		PopQueue popQueue = new PopQueue(queues, clientQueue, callPublisher);
		executors.execute(pushQueue);
		executors.execute(popQueue);
		MyNewTask myNewTask = new MyNewTask(asyncConfig);
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(myNewTask, 0, 1, TimeUnit.MINUTES);
	}

	private class MyNewTask implements Runnable {
		private AsyncConfig asyncConfig;

		public MyNewTask(AsyncConfig asyncConfig) {
			this.asyncConfig = asyncConfig;
		}
		@Override
		public void run() {
			Thread.currentThread().setName("Schedule-Thread");
			tblClientList = callPublisher.getAllClient();
			for (TblClient t : tblClientList) {
				if (!queues.containsKey(t.getTemplateId().toString())
						&& !clientQueue.containsKey(t.getTemplateId().toString())) {
					clientQueue.put(t.getTemplateId().toString(), t);
					BlockingQueue<CallFile> queue = new LinkedBlockingQueue<>();
					queues.put(t.getTemplateId().toString(), queue);
				}
			}
			List<String> templateId = tblClientList.stream().map(TblClient::getTemplateId).map(l -> l.toString()).collect(Collectors.toList());
			for(String key : clientQueue.keySet()) {
				if(!templateId.contains(key)) {
					clientQueue.remove(key);
					queues.remove(key);
				}
			}
			int size = tblClientList.size();
			if (size > 0 && size != asyncConfig.asyncTaskExecutor().getCorePoolSize()) {
				((ThreadPoolTaskExecutor) asyncConfig.asyncTaskExecutor()).setCorePoolSize(size);
				((ThreadPoolTaskExecutor) asyncConfig.asyncTaskExecutor()).setMaxPoolSize(size);
			}
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
			MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
			ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
			double cpuUsage = osBean.getProcessCpuLoad() * 100;
			long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() + memoryBean.getNonHeapMemoryUsage().getUsed();
	        long maxMemory = Runtime.getRuntime().maxMemory();
	        double memoryUsage = ((double) usedMemory / maxMemory) * 100;
	        log.info("CPU Usage: " + String.format("%.2f", cpuUsage) + "%");
	        log.info("Memory Usage: " + String.format("%.2f", memoryUsage) + "%");
	        for (ThreadInfo threadInfo : threadInfos) {
	            log.info("Thread name: " + threadInfo.getThreadName()+"    Thread state: " + threadInfo.getThreadState());
	        }
		}
	}
}
