package br.com.siecola.aws_batch_job_mw.controller;

import br.com.siecola.aws_batch_job_mw.exception.JobNotFoundException;
import br.com.siecola.aws_batch_job_mw.model.JobRequest;
import br.com.siecola.aws_batch_job_mw.model.JobResponse;
import br.com.siecola.aws_batch_job_mw.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createNewJob(
            @Valid @RequestBody JobRequest jobRequest) {

        JobResponse jobResponse = jobService.createNewJob(jobRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jobResponse);
    }


    @GetMapping
    public ResponseEntity<?> checkJobStatus(
            @Valid @PathVariable String jobId) {

        JobResponse jobResponse = null;
        try {
            jobResponse = jobService.checkJob(jobId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(jobResponse);
        } catch (JobNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("");
        }
    }
}