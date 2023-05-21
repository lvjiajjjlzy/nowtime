package org.lv.nowtime;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Festival {
    private int day = 0, b = 0;
    private String cachedHolidayName = null;

    public void tick() throws InterruptedException {
        ServerWorldEvents.LOAD.register((server, world) -> {
            try {
                updateFestival(server);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            int today = Integer.parseInt(currentDate.format(formatter));
            if (today != day) {
                day = today;
                updatePlayerScores(server);
                cachedHolidayName = null; /* 日期变化时清除缓存 */
            } else try {
                if (cachedHolidayName == null) cachedHolidayName = ApiParser.parseHolidayName();
                updateFestival(server);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            updatePlayerScores(server);
        });
    }

    private void updateFestival(MinecraftServer server) throws IOException, InterruptedException {
        if (cachedHolidayName == null) cachedHolidayName = ApiParser.parseHolidayName();
        switch (cachedHolidayName) {
            case "元旦" -> b = 1;
            case "春节" -> b = 2;
            case "清明节" -> b = 3;
            case "劳动节" -> b = 4;
            case "端午节" -> b = 5;
            case "中秋节" -> b = 6;
            case "国庆节" -> b = 7;
            default -> b = 0;
        }
    }

    private void updatePlayerScores(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        ScoreboardObjective objective = server.getScoreboard().getObjective("nowday");
        for (PlayerEntity player : players) {
            String playerName = player.getEntityName();
            ScoreboardPlayerScore scoreboardPlayerScore = server.getScoreboard().getPlayerScore(playerName, objective);
            scoreboardPlayerScore.setScore(b);
        }
    }
}