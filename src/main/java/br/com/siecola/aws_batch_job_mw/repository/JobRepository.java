package br.com.siecola.aws_batch_job_mw.repository;

import br.com.siecola.aws_batch_job_mw.model.Job;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

@EnableScan
@EnableScanCount
public interface JobRepository extends BaseRepository<Job, String> {

}