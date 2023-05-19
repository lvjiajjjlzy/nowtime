package org.lv.nowtime;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalTime;
import java.util.List;

import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK;


public class Nowtime extends Scoreboard implements ModInitializer {


<<<<<<< HEAD
=======
    private byte repeat = 0;

>>>>>>> 99d43a2bdcfe786998b9b7701e7c37991409870a
    @Override
    public void onInitialize() {
        Nowdate nowdate = new Nowdate();
        ServerTickEvents.START_SERVER_TICK.register(server -> nowdate.tick());

        START_SERVER_TICK.register(server -> {
            LocalTime now = LocalTime.now();
            int time = now.toSecondOfDay();

            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            ScoreboardObjective objective = server.getScoreboard().getObjective("nowtime");

            for (ServerPlayerEntity player : players) {
                String playerName = player.getEntityName();
                ScoreboardPlayerScore scoreboardPlayerScore = server.getScoreboard().getPlayerScore(playerName, objective);
                scoreboardPlayerScore.setScore(time);
            }
        });




 /*       ServerWorldEvents.LOAD.register((server, world) -> {
commands?
        });

*/
    }
}

