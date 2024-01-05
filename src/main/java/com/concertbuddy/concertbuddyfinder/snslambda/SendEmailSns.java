package com.concertbuddy.concertbuddyfinder.snslambda;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class SendEmailSns {
    public static final String TOPIC_ARN = "";
    public static final String ACCESS_KEY = "";
    public static final String SECRET_ACCESS_KEY = "";
    public static final String REGION = "";
    public static AmazonSNSClient client = (AmazonSNSClient) AmazonSNSClientBuilder
                    .standard()
                    .withRegion(REGION)
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(
                        ACCESS_KEY, SECRET_ACCESS_KEY)))
                    .build();

    public static void sendEmail(String email) {
        client.publish(TOPIC_ARN, email);
    }
}
