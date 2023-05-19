package org.lv.nowtime;


import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK;


public class Nowtime extends Scoreboard implements ModInitializer {


    private int repeat = 0;

    @Override
    public void onInitialize() {


        START_SERVER_TICK.register(server -> {
            LocalTime now = LocalTime.now();
            int time = now.toSecondOfDay();

            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            ScoreboardObjective objective = server.getScoreboard().getObjective("nowtime");

            for (PlayerEntity player : players) {
                String playerName = player.getEntityName();
                ScoreboardPlayerScore scoreboardPlayerScore = server.getScoreboard().getPlayerScore(playerName, objective);
                scoreboardPlayerScore.setScore(time);
            }
        });


        START_SERVER_TICK.register(server -> {
            repeat++;

            if (repeat == 20) {
                repeat = 0;

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
            }
        });
 /*       ServerWorldEvents.LOAD.register((server, world) -> {
commands?
        });

*/
    }
}

