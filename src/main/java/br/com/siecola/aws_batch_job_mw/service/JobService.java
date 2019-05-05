package br.com.siecola.aws_batch_job_mw.service;

import br.com.siecola.aws_batch_job_mw.enums.JobStatus;
import br.com.siecola.aws_batch_job_mw.enums.JobType;
import br.com.siecola.aws_batch_job_mw.exception.JobNotFoundException;
import br.com.siecola.aws_batch_job_mw.model.Job;
import br.com.siecola.aws_batch_job_mw.model.JobRequest;
import br.com.siecola.aws_batch_job_mw.model.JobResponse;
import br.com.siecola.aws_batch_job_mw.repository.JobRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private static final String JOB_ID_PREFIX = Headers.S3_USER_METADATA_PREFIX + "job-id";
    private AmazonS3 amazonS3;
    private JobRepository jobRepository;

    @Value("${amazon.aws.s3.bucket.name}")
    private String awsBucketName;

    @Autowired
    public JobService(AmazonS3 amazonS3, JobRepository jobRepository) {
        this.amazonS3 = amazonS3;
        this.jobRepository = jobRepository;
    }

    public JobResponse createNewJob(JobRequest jobRequest) {
        Instant expire = Instant.now().plus(Duration.ofMinutes(10));
        Job job = createJob(jobRequest, expire.getEpochSecond());
        String url = generateS3Url(job.getId(), expire);

        JobResponse jobResponse = new JobResponse();
        jobResponse.setJobId(job.getId());
        jobResponse.setUrl(url);
        jobResponse.setExpiresInSeconds(expire.getEpochSecond());
        return jobResponse;
    }

    public JobResponse checkJob(String jobId) throws JobNotFoundException {
        Optional<Job> optJob = jobRepository.findById(jobId);

        if (optJob.isPresent()) {
            Job job = optJob.get();

            JobResponse jobResponse = new JobResponse();
            jobResponse.setJobId(job.getId());
            jobResponse.setNumberOfRegisters(job.getNumberOfRegisters());
            jobResponse.setStatus(job.getStatus());
            return jobResponse;
        } else {
            log.error("Job {} not found", jobId);
            throw new JobNotFoundException();
        }
    }

    private String generateS3Url(String jobId, Instant expire) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(awsBucketName, jobId)
                .withMethod(HttpMethod.PUT)
                .withExpiration(Date.from(expire));

        generatePresignedUrlRequest.addRequestParameter(JOB_ID_PREFIX, jobId);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    private Job createJob(JobRequest jobRequest, long expiresInSeconds) {
        Job job = new Job();

        configureJob(job, jobRequest.getJobType());

        job.setType(jobRequest.getJobType());
        job.setUsername(jobRequest.getUsername());
        job.setNumberOfRegisters(0);
        job.setStatus(JobStatus.NEW);
        job.setTtl(expiresInSeconds);
        job.setAttempts(3);
        job.setDefinition("first-run-job-definition:2");
        job.setQueue("first-run-job-queue");

        return jobRepository.save(job);
    }

    private void configureJob(Job job, JobType jobType) {
        switch (jobType) {
            case LARGE:
                job.setMemory(3600);
                job.setvCpu(4);
                break;
            case MEDIUM:
                job.setMemory(2400);
                job.setvCpu(3);
                break;
            case SMALL:
                job.setMemory(1800);
                job.setvCpu(2);
                break;
        }
    }
}