package com.example.demo.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestJob {

	@Value("${demo.time}")
	private static long time;

	public static long getTime() {
		return time;
	}

	public static void setTime(long time) {
		TestJob.time = time;
	}

	//	@Scheduled(fixedRate = 2000)
	public void test() {
//		log.info("time : " + String.valueOf(new Date().getTime()));
//		time = System.currentTimeMillis();
		TestJob.setTime(System.currentTimeMillis());
//		log.info(String.valueOf(time));
	}
}
