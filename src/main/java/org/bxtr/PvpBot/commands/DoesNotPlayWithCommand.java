package org.bxtr.PvpBot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.PvpBot.Utils;
import org.bxtr.PvpBot.model.FightResult;
import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.model.Tournament;
import org.bxtr.PvpBot.model.TournamentParticipant;
import org.bxtr.PvpBot.repository.FightResultRepositoryJPA;
import org.bxtr.PvpBot.service.PlayerService;
import org.bxtr.PvpBot.service.TournamentParticipantService;
import org.bxtr.PvpBot.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
@Log4j2
public class DoesNotPlayWithCommand extends BotCommand {

    private final TournamentParticipantService tournamentParticipantService;

    private final TournamentService tournamentService;

    private final PlayerService playerService;

    private final FightResultRepositoryJPA fightResultRepositoryJPA;

    public DoesNotPlayWithCommand(TournamentParticipantService tournamentParticipantService,
                                  PlayerService playerService,
                                  FightResultRepositoryJPA fightResultRepositoryJPA,
                                  TournamentService tournamentService) {
        super("/not", "Покажет всех игроков, с которыми еще не играли в этот турнир.");
        this.tournamentParticipantService = tournamentParticipantService;
        this.playerService = playerService;
        this.fightResultRepositoryJPA = fightResultRepositoryJPA;
        this.tournamentService = tournamentService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(Utils.commandInputToString(user, chat, this.getCommandIdentifier(), strings));
        Player player = playerService.findPlayer("@" + Utils.safeToString(user.getUserName()));
        SendMessage sendMessage = new SendMessage().setChatId(chat.getId());
        if (player == null) {
            log.warn(String.format("Пользователь с ником %s не найдено", Utils.safeToString(user.getUserName())));
            Utils.send(absSender, sendMessage.setText(String.format("Игрок с ником @%s не найден",
                    Utils.safeToString(user.getUserName()))));
        }

        Tournament currentTournament = tournamentService.getCurrentTournament();
        List<TournamentParticipant> tournamentParticipant = tournamentParticipantService.getAll().stream()
                .filter(tournamentParticipant1 -> tournamentParticipant1.getTournament().getId().equals(currentTournament.getId()))
                .collect(Collectors.toList());

        Set<Player> allOtherPlayersInTournament = tournamentParticipant.stream().map(item -> item.getPlayer())
                .filter(item -> !player.equals(item))
                .collect(Collectors.toSet());

        List<FightResult> allRecordedFights = fightResultRepositoryJPA.findFightResultWith(player.getName());

        Set<Player> playersAlreadyPlayed = allRecordedFights.stream()
                .map(fight -> fight.getOne().equals(player) ? fight.getTwo() : fight.getOne())
                .collect(Collectors.toSet());

        allOtherPlayersInTournament.removeAll(playersAlreadyPlayed);

        if (allOtherPlayersInTournament.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Осталось сыграть с:\n");
            allOtherPlayersInTournament.forEach(otherPlayer ->
                    stringBuilder.append(Utils.safeToString(otherPlayer.getName())).append("\n"));
            sendMessage.setText(stringBuilder.toString());
        } else {
            sendMessage.setText("Сыграно со всеми участниками");
        }

        Utils.send(absSender, sendMessage);
    }
}
