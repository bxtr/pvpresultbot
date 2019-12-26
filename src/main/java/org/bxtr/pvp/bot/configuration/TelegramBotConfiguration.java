package org.bxtr.pvp.bot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Optional;
import java.util.stream.Stream;


@Configuration
public class TelegramBotConfiguration {

    @Value("${pvp_bot.proxy.host:#{null}}")
    private String proxyHost;

    @Value("${pvp_bot.proxy.port:#{null}}")
    private Integer proxyPort;

    @Value("${pvp_bot.proxy.user:#{null}}")
    private String proxyUser;

    @Value("${pvp_bot.proxy.password:#{null}}")
    private String proxyPassword;

    @Bean
    public DefaultBotOptions getBotOptions() {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        if (Stream.of(proxyHost, proxyPort)
                .map(Optional::ofNullable)
                .allMatch(Optional::isPresent)) {
            if (Stream.of(proxyUser, proxyPassword)
                    .map(Optional::ofNullable)
                    .allMatch(Optional::isPresent)) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                    }
                });
            }
            botOptions.setProxyHost(proxyHost);
            botOptions.setProxyPort(proxyPort);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        }

        return botOptions;
    }
}
