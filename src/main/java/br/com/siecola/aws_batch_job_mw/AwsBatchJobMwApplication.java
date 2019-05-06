package br.com.siecola.aws_batch_job_mw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;

@SpringBootApplication(
		scanBasePackages = {"br.com.siecola.aws_batch_job_mw.repository"},
		exclude = RepositoryRestMvcAutoConfiguration.class)
public class AwsBatchJobMwApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsBatchJobMwApplication.class, args);
	}
}
