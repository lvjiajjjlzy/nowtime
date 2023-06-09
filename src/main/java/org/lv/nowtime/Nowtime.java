package org.lv.nowtime;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.time.LocalTime;
import java.util.List;

import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK;

public class Nowtime extends Scoreboard implements ModInitializer {
    @Override
    public void onInitialize() {
        Nowdate nowdate = new Nowdate();
        Festival festival = new Festival();
        festival.tick();
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
    }
}
