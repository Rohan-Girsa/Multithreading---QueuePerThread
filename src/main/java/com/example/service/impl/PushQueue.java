package com.example.service.impl;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.example.dto.CallFile;
import com.example.service.CallPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushQueue implements Runnable {

	private ConcurrentHashMap<String, BlockingQueue<CallFile>> queues;
	private final CallPublisher callPublisher;
	private String defaultQueueKey = "0";

	public PushQueue(ConcurrentHashMap<String, BlockingQueue<CallFile>> queues, CallPublisher callPublisher) {
		System.out.println("Push Queue Constructor Called");
		this.queues = queues;
		this.callPublisher = callPublisher;
	}

	@Override
	public void run() {
	    Thread.currentThread().setName("Push-Thread");
	    while (true) {
	        try {
	            List<CallFile> data = callPublisher.getRecords();
	            if (data != null && !data.isEmpty()) {
	                for (CallFile callFile : data) {
	                    String templateId = callFile.gettemplateid().toString();
	                    BlockingQueue<CallFile> queue = queues.getOrDefault(templateId, queues.get(defaultQueueKey));
	                    queue.add(callFile);
	                    log.info("Adding Into Queue: [ ID: {} Msisdn: {} Context: {} Dnis: {} Queue Code: {} Template ID: {} Account ID: {} User ID: {} Status: {} Voice Log ID: {} ]",
	                            callFile.getid(), callFile.getani(), callFile.getcontext(), callFile.getdnis(),
	                            callFile.getqueuecode(), callFile.gettemplateid(), callFile.getaccountid(),
	                            callFile.getuserid(), callFile.getstatus(), callFile.getvoicelogid());
	                    log.info("Data added for ID: {} to the queue for template ID: {}", callFile.getid(), templateId);
	                }
	            } else {
	                log.info("Thread Push Queue Going To Sleep");
	                Thread.sleep(2000);
	            }
	        } catch (Exception e) {
	        	e.printStackTrace();
	            Thread.currentThread().interrupt();
	            log.error("Thread was interrupted. Aborting operation.", e);
	            return;
	        }
	    }
	}

}
