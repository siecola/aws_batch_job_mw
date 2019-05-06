package br.com.siecola.aws_batch_job_mw.consumer;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;

public class DynamoDBStreamsRecordProcessorFactory
        implements IRecordProcessorFactory {


    @Override
    public IRecordProcessor createProcessor() {
        return new JobRecordProcessor();
    }
}