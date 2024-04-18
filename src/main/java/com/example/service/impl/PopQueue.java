package com.example.service.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.example.dto.CallFile;
import com.example.entities.TblClient;
import com.example.service.CallPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PopQueue implements Runnable {
	private ConcurrentHashMap<String, BlockingQueue<CallFile>> queues;
	private ConcurrentHashMap<String, TblClient> clientQueue;
	private final CallPublisher callPublisher;

	public PopQueue(ConcurrentHashMap<String, BlockingQueue<CallFile>> queues,
			ConcurrentHashMap<String, TblClient> clientQueue, CallPublisher callPublisher) {
		System.out.println("Pop Queue Constructor Called");
		this.queues = queues;
		this.clientQueue = clientQueue;
		this.callPublisher = callPublisher;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("Pop-Thread");
		try {
			while (true) {
				for (String key : clientQueue.keySet()) {
					if (queues.containsKey(key)) {
						BlockingQueue<CallFile> queue = queues.get(key);
						if (queue != null && !queue.isEmpty()) {
							TblClient t = clientQueue.get(key);
							callPublisher.callPublisherRun(queue, t.getTps());
						} else {
							log.info("Pop Thread going to sleep");
							Thread.sleep(2000);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			log.info("Thread was interrupted. Aborting operation.", e);
			return;
		}
	}
}
