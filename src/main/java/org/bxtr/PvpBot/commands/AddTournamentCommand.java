package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.repository.TournamentCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.stream.Collectors;

@Log4j2
@Component
public class AddTournamentCommand extends BotCommand {

    @Autowired
    private TournamentCrudRepository tournamentCrudRepository;

    public AddTournamentCommand() {
        super("addTournament", "Добавит новый турнир");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(Utils.commandInputToString(user, chat, getCommandIdentifier(), arguments));
        SendMessage sendMessage = new SendMessage()
                .setChatId(chat.getId());
        if(arguments.length >= 1) {
            Tournament tournament = new Tournament();
            tournament.setTournamentName(Arrays.stream(arguments).collect(Collectors.joining( " ")));
            tournamentCrudRepository.save(tournament);
            sendMessage.setText(String.format("Tournament \"%s\" is added", tournament.getTournamentName()));
        } else {
            sendMessage.setText("Name of tournament is not valid. Example: /addTournament Sinister cup");
        }
        Utils.send(absSender, sendMessage);
    }
}
