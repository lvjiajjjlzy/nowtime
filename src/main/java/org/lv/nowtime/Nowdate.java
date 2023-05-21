package org.lv.nowtime;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK;

public class Nowdate {
    public void tick() {
        START_SERVER_TICK.register(server -> {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            int data = Integer.parseInt(currentDate.format(formatter));
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            ScoreboardObjective objective = server.getScoreboard().getObjective("nowdate");
            for (PlayerEntity player : players) {
                String playerName = player.getEntityName();
                ScoreboardPlayerScore scoreboardPlayerScore = server.getScoreboard().getPlayerScore(playerName, objective);
                scoreboardPlayerScore.setScore(data);
            }
        });
    }
}
