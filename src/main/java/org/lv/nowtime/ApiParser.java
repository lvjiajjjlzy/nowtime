package org.lv.nowtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
            holidayName = (holidayNameElement != null) ? holidayNameElement.getAsString() : "";
        } else {
            holidayName = "";
        }
        return holidayName;
    }

    private static String readFromUrl(String url) throws IOException, InterruptedException {
        int retryCount = 0, maxRetries = 3;
        while (retryCount < maxRetries) try (var stream = new URL(url).openStream()) {
            byte[] bytes = stream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            retryCount++;
            if (retryCount == maxRetries || !(e instanceof java.net.HttpRetryException)) throw e;
            else Thread.sleep(1000);
            if (((java.net.HttpRetryException) e).responseCode() == 429) {
                return "0";
            }
        }
        throw new IOException("Failed to read data from URL: " + url);
    }
}
