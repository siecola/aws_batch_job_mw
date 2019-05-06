package br.com.siecola.aws_batch_job_mw.consumer;

import br.com.siecola.aws_batch_job_mw.enums.JobStatus;
import br.com.siecola.aws_batch_job_mw.enums.JobType;
import br.com.siecola.aws_batch_job_mw.model.Job;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.streamsadapter.model.RecordAdapter;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class JobRecordProcessor implements IRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(JobRecordProcessor.class);

    private Integer checkpoint;

    @Override
    public void initialize(String shardId) {
        checkpoint = 0;
    }

    @Override
    public void processRecords(List<Record> records,
                               IRecordProcessorCheckpointer checkpointer) {
        for (Record record : records) {
            if (record instanceof RecordAdapter) {
                com.amazonaws.services.dynamodbv2.model.Record streamRecord =
                        ((RecordAdapter) record).getInternalObject();

                Map<String, AttributeValue> newImage = streamRecord.getDynamodb()
                        .getNewImage();
                Job job = buildJob(newImage);

                if ("MODIFY".equals(streamRecord.getEventName())) {

                    LOG.info("Received JobId: {} - Sending notification...",
                            job.getId());
                } else if ("REMOVE".equals(streamRecord.getEventName())) {

                    LOG.info("Removed JobId: {} - Sending notification...",
                            job.getId());
                }
                break;
            }
            checkpoint += 1;
            if (checkpoint % 5 == 0) {
                try {
                    checkpointer.checkpoint();
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            }
        }

    }

    private Job buildJob(Map<String, AttributeValue> newImage) {
        Job job = new Job();
        job.setId(newImage.get(Job.ID).getS());
        job.setStatus(JobStatus.valueOf(newImage.get(Job.STATUS).getS()));
        job.setNumberOfRegisters(Integer.parseInt(newImage.get(Job.NUM_REGISTERS).getN()));
        job.setType(JobType.valueOf(newImage.get(Job.TYPE).getS()));
        job.setvCpu(Integer.parseInt(newImage.get(Job.VCPU).getN()));
        job.setMemory(Integer.parseInt(newImage.get(Job.MEMORY).getN()));
        job.setAttempts(Integer.parseInt(newImage.get(Job.ATTEMPTS).getN()));
        job.setQueue(newImage.get(Job.QUEUE).getS());
        job.setDefinition(newImage.get(Job.DEFINITION).getS());
        job.setTtl(Long.parseLong(newImage.get(Job.TTL).getN()));
        job.setUsername(newImage.get(Job.USERNAME).getS());
        return job;
    }

    @Override
    public void shutdown(IRecordProcessorCheckpointer checkpointer,
                         ShutdownReason reason) {
        if (reason == ShutdownReason.TERMINATE) {
            try {
                checkpointer.checkpoint();
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }
}