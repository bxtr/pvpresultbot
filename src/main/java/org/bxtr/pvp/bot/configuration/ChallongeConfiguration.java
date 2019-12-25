package org.bxtr.pvp.bot.configuration;

import at.stefangeyer.challonge.Challonge;
import at.stefangeyer.challonge.model.Credentials;
import at.stefangeyer.challonge.rest.retrofit.RetrofitRestClient;
import at.stefangeyer.challonge.serializer.gson.GsonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ChallongeConfiguration {

    @Value("${pvp_bot.challonge.username}")
    private String userName;

    @Value("${pvp_bot.challonge.token}")
    private String token;

    @Bean
    public Challonge getChallonge() {
        Credentials credentials = new Credentials(userName, token);
        return new Challonge(credentials, new GsonSerializer(), new RetrofitRestClient());
    }
}
