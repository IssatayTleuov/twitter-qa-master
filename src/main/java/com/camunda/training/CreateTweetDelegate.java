package com.camunda.training;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class CreateTweetDelegate implements JavaDelegate {
    private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class);

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String content = "I did it! Cheers Issatay";
        LOGGER.info("Publishing tweet: " + content);
        AccessToken accessToken = new AccessToken("220324559-CO8TfUmrcoCrvFHP4TacgToN5hLC8cMw4n2EwmHo", "WvVureFv5TBWTGhESgGe3fqZM7XbGMuyIhxB84zgcoUER");
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("lRhS80iIXXQtm6LM03awjvrvk", "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS");
        twitter.setOAuthAccessToken(accessToken);
        twitter.updateStatus(content);
    }
}
