package org.lv.nowtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class ApiParser {
    private static String holidayName;

    public static String getHolidayName() {
        return holidayName;
    }

    public static String parseHolidayName() throws IOException, InterruptedException {
        String url = "https://timor.tech/api/holiday/info", jsonText = readFromUrl(url);
        JsonObject jsonObject = JsonParser.parseString(jsonText).getAsJsonObject();
        JsonElement holidayElement = jsonObject.get("holiday");
        if (holidayElement != null && holidayElement.isJsonObject()) {
            JsonObject holidayObject = holidayElement.getAsJsonObject();
            JsonElement holidayNameElement = holidayObject.get("name");
            if (holidayNameElement != null) {
                String holidayName = holidayNameElement.getAsString();                /* 检测是否为圣诞节 */
                if (isChristmas()) {
                    holidayName = "圣诞节";
                }
                ApiParser.holidayName = holidayName;
            } else {
                ApiParser.holidayName = "";
            }
        } else {
            ApiParser.holidayName = "";
        }
        return holidayName;
    }

    private static String readFromUrl(String url) throws IOException, InterruptedException {
        int retryCount = 0, maxRetries = 1;
        while (retryCount < maxRetries) {
            try (var stream = new URL(url).openStream()) {
                byte[] bytes = stream.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    throw e;
                }
                // 判断是否为网络连接问题
                if (e instanceof java.net.ConnectException
                        || e instanceof java.net.SocketTimeoutException
                        || e instanceof java.net.UnknownHostException) {
                    Thread.sleep(1000000); // 休眠1000秒后进行重试
                } else {
                    throw e;
                }
            }
        }
        throw new IOException("获取节日信息链接: " + url);
    }

    private static boolean isChristmas() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue(), day = today.getDayOfMonth();
        return (month == 12 && day >= 24 && day <= 26);
    }
}