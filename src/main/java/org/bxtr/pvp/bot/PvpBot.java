package org.bxtr.pvp.bot;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.service.PlayerService;
import org.bxtr.pvp.bot.service.TeamService;
import org.bxtr.pvp.bot.service.TournamentService;
import org.bxtr.pvp.bot.domain.Team;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Log4j2
@Component
public class PvpBot extends TelegramLongPollingCommandBot {

    private static final Integer CACHETIME = 86400;
    private final PlayerService playerService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final List<BotCommand> botCommands;
    @Value("${pvp-bot.telegram.token}")
    private String token;


    public PvpBot(List<BotCommand> botCommands,
                  DefaultBotOptions options, TournamentService tournamentService, TeamService teamService, PlayerService playerService) {
        super(options);

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot.");
            Utils.send(absSender, commandUnknownMessage);
        });

        this.playerService = playerService;
        this.tournamentService = tournamentService;
        this.teamService = teamService;
        this.botCommands = botCommands;
        botCommands.forEach(c -> {
            log.info(String.format("/%s - %s", c.getCommandIdentifier(), c.getDescription()));
            register(c);
        });

    }

    private static AnswerInlineQuery converteResultsToResponse(InlineQuery inlineQuery, List<Player> results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHETIME);
        answerInlineQuery.setResults(convertRaeResults(results));
        return answerInlineQuery;
    }

    private static List<InlineQueryResult> convertRaeResults(List<Player> raeResults) {
        List<InlineQueryResult> results = new ArrayList<>();
        for (int i = 0; i < raeResults.size(); i++) {
            for (String score : Arrays.asList("2 1", "1 2", "2 0", "0 2")) {
                Player player = raeResults.get(i);
                InputTextMessageContent messageContent = new InputTextMessageContent();
                messageContent.disableWebPagePreview();
                messageContent.enableMarkdown(true);
                messageContent.setMessageText("/short " + player.getName() + " " + score);
                InlineQueryResultArticle article = new InlineQueryResultArticle();
                article.setInputMessageContent(messageContent);
                article.setId(Integer.toString(i) + "#" + score);
                article.setTitle(player.getName());
                article.setDescription(score);
                results.add(article);
            }
        }

        return results;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return "PvpResultBotDev";
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasInlineQuery()) {
            handleIncomingInlineQuery(update.getInlineQuery());
        } else if (update.hasCallbackQuery()) {
            log.debug("callback query");
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            String userName = update.getMessage().getFrom().getUserName();
            log.info(String.format("@%s загружает команду", userName));
            Player player = playerService.findPlayer("@" + userName);
            if (player != null) {
                PhotoSize photo = getPhoto(update);
                String filePath = getFilePath(photo);
                java.io.File file = downloadPhotoByFilePath(filePath);
                Team team = new Team();
                team.setPlayer(player);
                team.setTournament(tournamentService.getCurrentTournament());
                try {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    team.setImage(fileContent);
                } catch (IOException e) {
                    e.printStackTrace();
                    SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(),
                            "Произошла ошибка во время сохранения команды.");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e1) {
                        e1.printStackTrace();
                    }
                    return;
                }
                teamService.save(team);
                try {
                    execute(new SendMessage(update.getMessage().getChatId(), "Команда загружена"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            log.info("text message:" + Utils.safeToString(message));
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), message);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleIncomingInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        log.info(String.format("Searching: %s", query));
        try {
            if (!query.isEmpty() && query.length() > 1) {
                List<Player> results = playerService.findLike(query);
                if (results.size() > 0)
                    execute(converteResultsToResponse(inlineQuery, results));
            } else {
                execute(converteResultsToResponse(inlineQuery, new ArrayList<>()));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private PhotoSize getPhoto(Update update) {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        return photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
    }

    public String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);

        if (photo.hasFilePath()) {
            return photo.getFilePath();
        } else {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(photo.getFileId());
            try {
                File file = execute(getFileMethod);
                return file.getFilePath();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public java.io.File downloadPhotoByFilePath(String filePath) {
        try {
            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }
}
