package com.example.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.example.dto.CallFile;
import com.example.entities.TblClient;

public interface CallPublisher {
	void callPublisherRun(BlockingQueue<CallFile> queue,int tps);
	List<TblClient> getAllClient();
	List<CallFile> getRecords();
}
