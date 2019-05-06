package br.com.siecola.aws_batch_job_mw.config;

import br.com.siecola.aws_batch_job_mw.consumer.DynamoDBStreamsRecordProcessorFactory;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.streamsadapter.AmazonDynamoDBStreamsAdapterClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.util.EC2MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DynamoDBStreamConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory
            .getLogger(DynamoDBStreamConfig.class);

    @Value("${amazon.aws.region}")
    private String awsRegion;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    private static Worker worker;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing DynamoDB streaming");

        String awsDynamoDBStreamEndpoint = "streams.dynamodb." + awsRegion +
                ".amazonaws.com";
        String awsCloudWatchEndpoint = "monitoring." + awsRegion +
                ".amazonaws.com";

        DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain =
                new DefaultAWSCredentialsProviderChain();
        DynamoDBStreamsRecordProcessorFactory dynamoDBStreamsRecordProcessorFactory =
                new DynamoDBStreamsRecordProcessorFactory();

        AmazonDynamoDBStreamsAdapterClient amazonDynamoDBStreamsAdapterClient =
                new AmazonDynamoDBStreamsAdapterClient(defaultAWSCredentialsProviderChain,
                        new ClientConfiguration());
        amazonDynamoDBStreamsAdapterClient.setEndpoint(awsDynamoDBStreamEndpoint);

        AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(awsCloudWatchEndpoint, awsRegion))
                .withCredentials(defaultAWSCredentialsProviderChain)
                .build();

        String latestStreamArn = describeLatestStreamArn(amazonDynamoDB, "job");

        String workerId = "job-worker-" + EC2MetadataUtils.getInstanceId();

        KinesisClientLibConfiguration workerConfig =
                new KinesisClientLibConfiguration("job-worker",
                        latestStreamArn, defaultAWSCredentialsProviderChain,
                        workerId)
                .withMaxRecords(10)
                .withIdleTimeBetweenReadsInMillis(1000)
                .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);

        worker = new Worker(dynamoDBStreamsRecordProcessorFactory, workerConfig,
                amazonDynamoDBStreamsAdapterClient, amazonDynamoDB,
                amazonCloudWatch);

        worker.run();
        log.info("DynamoDB stream started");
    }

    private static String describeLatestStreamArn(AmazonDynamoDB amazonDynamoDB,
                                                  String tableName) {
        return amazonDynamoDB.describeTable(new DescribeTableRequest()
                .withTableName(tableName)).getTable().getLatestStreamArn();
    }
}
