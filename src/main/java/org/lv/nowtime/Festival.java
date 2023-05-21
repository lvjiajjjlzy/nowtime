/*
这是一个表示节日信息的类
This is a class that represents festival information.
*/
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
    /*
   在此方法中，将为“LOAD”事件注册一个新的侦听器，该侦听器将获取服务器的世界和服务并尝试更新节日信息
   In this method, a new listener for the "LOAD" event is registered here, which obtains the world and the server of the server, and attempts to update the festival information.
   */
    public void tick() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            try {
                updateFestival(server);
            } catch (IOException | InterruptedException e) {
                System.err.println("无法更新节日信息：" + e.getMessage());
            }
        });
         /*
        在此方法中，为“START_SERVER_TICK”事件注册新的侦听器，该侦听器将获取当前日期并尝试根据日期更新节日信息和玩家分数。
        In this method, a new listener for the "START_SERVER_TICK" event is registered here, which obtains the current date and attempts to update the festival information and player scores based on the date.
        */
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            int today = Integer.parseInt(currentDate.format(formatter));
             /*
            如果日期与之前的一天不同，则开始尝试更新节日信息和玩家分数
            If the date is different from the previous day, start trying to update the festival information and player scores.
            */
            if (today != day) {
                day = today;
                updatePlayerScores(server);
                cachedHolidayName = null; /* 日期变化时清除缓存 */
            } else try {
                if (cachedHolidayName == null) cachedHolidayName = ApiParser.parseHolidayName();
                updateFestival(server);
            } catch (IOException | InterruptedException e) {
                return;
            }
            updatePlayerScores(server);
        });
    }
    /*
   此方法将根据从ApiParser.parseHolidayName()获取的当前节日名称更新b的值
   This method will update the value of b based on the current holiday name obtained from ApiParser.parseHolidayName().
   */
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
            case "圣诞节" -> b = 8;
            default -> b = 0;
        }
    }
    /*
   此方法将更新每个玩家的ScoreboardPlayerScore值
   This method will update ScoreboardPlayerScore value for each user.
   */
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