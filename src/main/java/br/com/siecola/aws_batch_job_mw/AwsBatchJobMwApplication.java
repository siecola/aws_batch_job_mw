package br.com.siecola.aws_batch_job_mw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AwsBatchJobMwApplication {

	private static final Logger log = LoggerFactory.getLogger(AwsBatchJobMwApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AwsBatchJobMwApplication.class, args);

		log.info("Starting AwsBatchJobMwApplication application");
	}
}
