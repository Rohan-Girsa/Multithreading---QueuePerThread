package com.example.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.dto.CallFile;
import com.example.entities.TblClient;
import com.example.repo.TblBaseRepo;
import com.example.repo.TblClientRepo;
import com.example.service.CallPublisher;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CallPublisherImpl implements CallPublisher {
	@Autowired
	private TblClientRepo tblClientRepo;
	@Autowired
	private TblBaseRepo tblBaseRepo;

	@Override
	@Async(value = "AsyncTaskExecutor")
	public void callPublisherRun(BlockingQueue<CallFile> queue, int tps) {
		int index = 0;
		try {
			while (queue != null && queue.peek() != null) {
				CallFile data = queue.take();
				if (data != null) {
					log.info("TPS: " + tps + " Call File: [ ID: " + data.getid() + " Msisdn: " + data.getani()
							+ " Context: " + data.getcontext() + " Dnis: " + data.getdnis() + " Queue Code: "
							+ data.getqueuecode() + " Template ID: " + data.gettemplateid() + " Account ID: "
							+ data.getaccountid() + " User ID: " + data.getuserid() + " Status: " + data.getstatus()
							+ " Voice Log ID: " + data.getvoicelogid() + "]");
					index++;
					System.out.println(Thread.currentThread().getName() + " Count: " + index);
				} else {
					log.info(Thread.currentThread().getName() + " going to sleep!!!");
					Thread.sleep(2000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<TblClient> getAllClient() {
		try {
			List<TblClient> list = tblClientRepo.findAll();
			return list != null ? list : Collections.emptyList();
		} catch (Exception e) {
			System.out.println("In All client exception: "+e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public List<CallFile> getRecords() {
		try {
			Optional<List<CallFile>> tbListOptional = tblBaseRepo.findData();
			if (tbListOptional.isPresent()) {
	            List<CallFile> tbList = tbListOptional.get();
	            List<Long> idList = tbList.stream().map(CallFile::getid).map(Long::valueOf).collect(Collectors.toList());
				tblBaseRepo.deleteFromList(idList);
				return tbList;
			}
		} catch (Exception e) {
			System.out.println("In exception@@@@@@");
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

}
