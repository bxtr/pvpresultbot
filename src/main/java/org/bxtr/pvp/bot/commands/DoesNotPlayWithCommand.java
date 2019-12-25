package org.bxtr.pvp.bot.commands;

import lombok.extern.log4j.Log4j2;
import org.bxtr.pvp.bot.repository.FightResultRepositoryJPA;
import org.bxtr.pvp.bot.Utils;
import org.bxtr.pvp.bot.domain.FightResult;
import org.bxtr.pvp.bot.domain.Player;
import org.bxtr.pvp.bot.domain.Tournament;
import org.bxtr.pvp.bot.domain.TournamentParticipant;
import org.bxtr.pvp.bot.service.PlayerService;
import org.bxtr.pvp.bot.service.TournamentParticipantService;
import org.bxtr.pvp.bot.service.TournamentService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Log4j2
@Component
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
