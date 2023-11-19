package com.concertbuddy.concertbuddyfinder.snslambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;

import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.concertbuddy.concertbuddyfinder.models.LambdaResponse;

import lombok.extern.log4j.Log4j2;

import java.net.HttpURLConnection;
import java.util.Optional;

/**
 * Send email lambda
 *
 */
@Log4j2
public class SendEmailLambda implements RequestHandler<SNSEvent, LambdaResponse>
{
    public static final String FROM = "violetforb123@gmail.com";
    public static final String SUBJECT = "Matches Available";
    public static final String HTMLBODY = "<h1>Your matches are ready</h1> <p>Please visit the website to view your matches.</p>";
    public static final String TEXTBODY = "Your matches are ready. Please visit the website to view your matches.";

    public LambdaResponse handleRequest(SNSEvent event, Context context) {
        log.info("Lambda handler for SNS events");
        try {
            Optional<SNSEvent.SNSRecord> snsRecord = event.getRecords().stream().findAny();
            if(snsRecord.isPresent()){
                final SNSEvent.SNS sns = snsRecord.get().getSNS();
                final String email = sns.getMessage();
                log.info("Send email to: {}", email);

                Destination destination = new Destination().withToAddresses(email);
                Message message = new Message()
                .withBody(new Body()
                    .withHtml(new Content()
                        .withCharset("UTF-8").withData(HTMLBODY))
                    .withText(new Content()
                        .withCharset("UTF-8").withData(TEXTBODY)))
                .withSubject(new Content()
                    .withCharset("UTF-8").withData(SUBJECT));
                
                SendEmailRequest sendEmailReq = new SendEmailRequest()
                    .withSource(FROM)
                    .withDestination(destination)
                    .withMessage(message);

                AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion("us-east-2").build();
                client.sendEmail(sendEmailReq);
                log.info("Email sent");
            }

            return LambdaResponse.builder()
                    .httpCode(HttpURLConnection.HTTP_OK)
                    .message("OK.")
                    .build();
        } catch (Exception e){
            log.info("exception", e);
            return LambdaResponse.builder()
                    .httpCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .message("Something went wrong.")
                    .build();
        }
    }
}
