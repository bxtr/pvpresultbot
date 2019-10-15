package org.bxtr.PvpBot;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.commands.*;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.model.Team;
import org.bxtr.PvpBot.service.PlayerService;
import org.bxtr.PvpBot.service.TeamService;
import org.bxtr.PvpBot.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Log4j2
@Component
@Scope("singleton")
public class PvpBot extends TelegramLongPollingCommandBot {

    private final TelegramBotsApi telegramBotsApi;
    private final AddPlayerCommand addPlayerCommand;
    private final AddFightResultCommand addFightResultCommand;
    private final AllFightResultCommand allFightResultCommand;
    private final AllPlayersCommand allPlayersCommand;
    private final HelpCommand helpCommand;
    private final LeaderboardCommand leaderboardCommand;
    private final UpdateResultsOnChallongeCommand updateResultsOnChallongeCommand;
    private final FriendCodeListCommand friendCodeListCommand;
    private final AddFightResultShortCommand addFightResultShortCommand;
    private final DoesNotPlayWithCommand doesNotPlayWithCommand;
    private final RegisterPlayerCommand registerPlayerCommand;

    private final PlayerService playerService;
    private final TeamService teamService;
    private final TournamentService tournamentService;

    private static final Integer CACHETIME = 86400;

    @Value("${pvpbot.telegram.token}")
    private String TOKEN;


    public PvpBot(@Autowired DefaultBotOptions options, TelegramBotsApi telegramBotsApi, AddFightResultShortCommand addFightResultShortCommand,
                  AddFightResultCommand addFightResultCommand, AllFightResultCommand allFightResultCommand,
                  AllPlayersCommand allPlayersCommand, HelpCommand helpCommand, LeaderboardCommand leaderboardCommand,
                  UpdateResultsOnChallongeCommand updateResultsOnChallongeCommand, FriendCodeListCommand friendCodeListCommand,
                  DoesNotPlayWithCommand doesNotPlayWithCommand, RegisterPlayerCommand registerPlayerCommand,
                  AddPlayerCommand addPlayerCommand,
                  PlayerService playerService, TournamentService tournamentService, TeamService teamService) {
        super(options, "PvpResultBot");

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot.");
            Utils.send(absSender, commandUnknownMessage);
        });
        this.telegramBotsApi = telegramBotsApi;
        this.addFightResultShortCommand = addFightResultShortCommand;
        this.addPlayerCommand = addPlayerCommand;
        this.addFightResultCommand = addFightResultCommand;
        this.allFightResultCommand = allFightResultCommand;
        this.allPlayersCommand = allPlayersCommand;
        this.helpCommand = helpCommand;
        this.leaderboardCommand = leaderboardCommand;
        this.updateResultsOnChallongeCommand = updateResultsOnChallongeCommand;
        this.friendCodeListCommand = friendCodeListCommand;
        this.doesNotPlayWithCommand = doesNotPlayWithCommand;
        this.registerPlayerCommand = registerPlayerCommand;
        this.playerService = playerService;
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    @PostConstruct
    private void postConstruct() {
        try {
            telegramBotsApi.registerBot(this);
            registerLog(addPlayerCommand);
            registerLog(addFightResultCommand);
            registerLog(allPlayersCommand);
            registerLog(allFightResultCommand);
            registerLog(helpCommand);
            registerLog(leaderboardCommand);
            registerLog(updateResultsOnChallongeCommand);
            registerLog(friendCodeListCommand);
            registerLog(addFightResultShortCommand);
            registerLog(doesNotPlayWithCommand);
            registerLog(registerPlayerCommand);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private void registerLog(BotCommand botCommand) {
        log.info(String.format("/%s - %s", botCommand.getCommandIdentifier(), botCommand.getDescription()));
        register(botCommand);
    }

    @Override
    public String getBotToken() {
        return TOKEN;
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
                team.setTournament(tournamentService.getTournament());
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
            if (!query.isEmpty()) {
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
