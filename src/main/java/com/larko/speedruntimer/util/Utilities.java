package com.larko.speedruntimer.util;

import com.larko.speedruntimer.SpeedrunTimer;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Utilities {
    public static String formatTimer(Long time) {
        return (new SimpleDateFormat("mm:ss:SSS")).format(new Date(time));
    }
    public static boolean pauseTimerOnPauseMenu = false;

    public static final HashMap<RegistryKey<World>, String> dimensionNames = new HashMap<RegistryKey<World>, String>(){{
        put(World.OVERWORLD, "Overworld");
        put(World.THE_NETHER, "Nether");
        put(World.THE_END, "End");
    }};

    public static final HashMap<RegistryKey<World>, String> dimensionColorTable = new HashMap<RegistryKey<World>, String>(){{
        put(World.OVERWORLD, "639C2F");
        put(World.THE_NETHER, "E64C19");
        put(World.THE_END, "FBFFD9");
    }};

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
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler((user) -> SpeedrunTimer.LOGGER.info("connected to discord : " + user.userId)).build();
        DiscordRPC.discordInitialize("775406152619393115", handlers, false);
        //DiscordRPC.discordRegister("775406152619393115", "");
    }

}
