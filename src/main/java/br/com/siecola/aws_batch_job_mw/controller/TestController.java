package br.com.siecola.aws_batch_job_mw.controller;

import br.com.siecola.aws_batch_job_mw.model.Job;
import br.com.siecola.aws_batch_job_mw.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    private JobRepository jobRepository;

    public TestController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/create")
    public ResponseEntity<?> create() {
        log.info("Creating jobs");

        Job job = new Job();
        job.setUsername("hannah");
        job.setAttempts(4);
        job.setDefinition("678");
        jobRepository.save(job);


        job = new Job();
        job.setUsername("clotilde");
        job.setAttempts(5);
        job.setDefinition("098");
        jobRepository.save(job);

        return ResponseEntity.ok("");
    }

    @RequestMapping(method = RequestMethod.GET, path = "/test/{name}")
    public ResponseEntity<?> test(@PathVariable String name) {
        log.info("Test controller - name: {}", name);

        List<Job> jobs = jobRepository.findAll();

        return ResponseEntity.ok("Name(1): " + name);
    }
}
