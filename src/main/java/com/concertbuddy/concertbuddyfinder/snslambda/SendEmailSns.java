package com.concertbuddy.concertbuddyfinder.snslambda;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class SendEmailSns {
    public static final String TOPIC_ARN = "arn:aws:sns:us-east-2:290208614659:SendEmail";
    public static final String ACCESS_KEY = "AKIAUHEOIBEB3G42H6AA";
    public static final String SECRET_ACCESS_KEY = "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";
    public static final String REGION = "us-east-2";
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
