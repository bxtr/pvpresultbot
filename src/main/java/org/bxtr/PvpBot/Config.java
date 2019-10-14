package org.bxtr.PvpBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;


@EnableJpaRepositories
@Configuration
class Config {

    @Value("${pvpbot.proxy.host}")
    private String PROXY_HOST;

    @Value("${pvpbot.proxy.port}")
    private int PROXY_PORT;

    @Value("${pvpbot.proxy.enable}")
    private boolean proxyIsEnable;

    @Bean
    @Scope("singleton")
    public TelegramBotsApi getTelegramBotsApi() {
        return new TelegramBotsApi();
    }

    @Bean
    @Scope("singleton")
    public DefaultBotOptions getBotOptions() {
        ApiContextInitializer.init();
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        if (proxyIsEnable) {
            botOptions.setProxyHost(PROXY_HOST);
            botOptions.setProxyPort(PROXY_PORT);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS4);
        }
        return botOptions;
    }
}
