package com.larko.speedruntimer.util;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {
    public static String formatTimer(Long time) {
        return (new SimpleDateFormat("mm:ss:SSS")).format(new Date(time));
    }

    public static String colorDimension(String dimension) {
        if(dimension == "Overworld") {
            return "639C2F";
        } else if(dimension == "Nether") {
            return "E64C19";
        } else {
            return "FBFFD9";
        }
    }

    public static String readableStep(String step) {
        if(step == "enterNether") {
            return "Entered Nether";
        } else if(step == "exitNether") {
            return "Left Nether";
        } else {
            return "Entered End";
        }
    }

    public static void initDiscord() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> System.out.println(user.username)).build();
        DiscordRPC.discordInitialize("775406152619393115", handlers, false);
        //DiscordRPC.discordRegister("775406152619393115", "");
    }
}
