package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.model.FightResult;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.model.TournamentParticipant;
import org.bxtr.PvpBot.repository.FightResultRepositoryJPA;
import org.bxtr.PvpBot.repository.TournamentParticipantRepository;
import org.bxtr.PvpBot.service.PlayerService;
import org.bxtr.PvpBot.service.TournamentParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
@Log4j2
public class DoesNotPlayWithCommand extends BotCommand {

    @Autowired
    private TournamentParticipantService tournamentParticipantService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private FightResultRepositoryJPA fightResultRepositoryJPA;

    public DoesNotPlayWithCommand() {
        super("/not", "Покажет всех игроков, с которыми еще не играли в этот турнир");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, this.getCommandIdentifier(), strings));
        Player player = playerService.findPlayer("@" + Utils.safeToString(user.getUserName()));
        if(player == null)
            return;

        List<TournamentParticipant> tournamentParticipant = tournamentParticipantService.getAll();
        Set<Player> allOtherPlayersInTournament = tournamentParticipant.stream().map(item -> item.getPlayer())
                .filter(item -> !player.equals(item))
                .collect(Collectors.toSet());

        List<FightResult> allRecordedFights = fightResultRepositoryJPA.findFightResultWith(player.getName());

        Set<Player> playersAlreadyPlayed = allRecordedFights.stream()
                .map(fight -> fight.getOne().equals(player) ? fight.getTwo() : fight.getOne())
                .collect(Collectors.toSet());

        allOtherPlayersInTournament.removeAll(playersAlreadyPlayed);

        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        if(allOtherPlayersInTournament.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Осталось сыграть с:\n");
            allOtherPlayersInTournament.forEach(otherPlayer ->
                    stringBuilder.append(Utils.safeToString(otherPlayer.getName())).append("\n"));
            sendMessage.setText(stringBuilder.toString());
        } else {
            sendMessage.setText("Сыграно со всеми участниками");
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
