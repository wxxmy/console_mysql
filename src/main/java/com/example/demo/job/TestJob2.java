package com.example.demo.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestJob2 {

//	@Value("${demo.time}")
//	private static long time;
//
//	public long getTime() {
//		return time;
//	}
//
//	public TestJob2 setTime(long time) {
//		this.time = time;
//		return this;
//	}

	//	@Scheduled(fixedRate = 2000)
	public void test() {
//		log.info("time : " + String.valueOf(new Date().getTime()));
//		log.info(String.valueOf(TestJob.getTime()));
		log.info("time: " + TestJob.getTime());
	}
}
