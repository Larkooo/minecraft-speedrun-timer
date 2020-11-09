package com.larko.speedruntimer.events;

import com.larko.speedruntimer.SpeedrunTimer;
import com.larko.speedruntimer.util.Utilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Date;
import java.util.LinkedHashMap;


@Mod.EventBusSubscriber(modid= SpeedrunTimer.MOD_ID, bus=Mod.EventBusSubscriber.Bus.FORGE, value= Dist.CLIENT)
public class ModClientEvents {

    public static Long startTime = 0L;

    public static Long finishTime = 0L;

    public static String actualDimension;

    public static LinkedHashMap<String, Long> steps = new LinkedHashMap<String, Long>();

    public static void startSpeedrun() {
        startTime = new Date().getTime();
        DiscordRichPresence rich = new DiscordRichPresence.Builder("No steps accomplished yet")
                .setDetails("Dimension : " + actualDimension)
                .setStartTimestamps(startTime)
                .setBigImage("mc", "Made with love by Larko")
                .setSmallImage("clockicon", "Speedrunning")
                .build();
        DiscordRPC.discordUpdatePresence(rich);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Text event) {
        String startText = startTime > 0 ? finishTime > 0 ? Utilities.formatTimer(finishTime - startTime) : Utilities.formatTimer(new Date().getTime() - startTime) : "Move to start the timer";
        //System.out.println(startText);
        //System.out.println(startTime);
        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontRenderer = mc.fontRenderer;
        MatrixStack matrixStack = event.getMatrixStack();
        //matrixStack.push();
        //matrixStack.scale(1.3F,1.3F, 1.3F);
        //fontRenderer.drawStringWithShadow(matrixStack, finishTime > 0 ? "Speedrun Timer - Finished :o" : "Speedrun Timer", 2,2,Integer.parseInt("349EEB", 16));
        //matrixStack.pop();
        matrixStack.push();
        matrixStack.scale(2.5F,2.5F,2.5F);
        fontRenderer.drawStringWithShadow(matrixStack, startText, 1,1,Integer.parseInt(Utilities.colorDimension(actualDimension), 16));
        matrixStack.pop();
        if(!steps.isEmpty()) {
            String stepsString = "";
            int y = 28;
            for(String step : steps.keySet()) {
                fontRenderer.drawStringWithShadow(matrixStack, "[ " + Utilities.readableStep(step) + " : " + Utilities.formatTimer(steps.get(step)) + " ]", 4,y,Integer.parseInt("FFFFFF", 16));
                y += 10;
            }

        }
    }

    //@SubscribeEvent
    //public static void GuiScreen(GuiScreenEvent.InitGuiEvent.Post event) {
    //    event.addWidget(n);
    //}

    @SubscribeEvent
    public static void onPlayerMove(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        //System.out.println(player.getPosX());
        if ((player.getPosX() - player.prevPosX > 0) || (player.getPosY() - player.prevPosY > 0)) {
            if(startTime == 0)
                startSpeedrun();
                SpeedrunTimer.LOGGER.info("player moved, starting speedrun");
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if(startTime == 0)
            startSpeedrun();
            SpeedrunTimer.LOGGER.info("player interacted, starting speedrun");
    }

    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        DiscordRPC.discordClearPresence();
        steps.clear();
        startTime = 0L;
        finishTime = 0L;
        SpeedrunTimer.LOGGER.info("player logged out, clearing everything");
    }

    @SubscribeEvent
    public static void onGameFinish(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        //System.out.println(entity.getName());
        if(entity instanceof EnderDragonEntity) {
            finishTime = new Date().getTime();
            SpeedrunTimer.LOGGER.info("finished game " + finishTime);
            DiscordRichPresence rich = new DiscordRichPresence.Builder("Finished the game at " + Utilities.formatTimer(finishTime))
                    .setDetails("Dimension : " + actualDimension)
                    .setStartTimestamps(startTime)
                    .setBigImage("mc", "Made with love by Larko")
                    .setSmallImage("clockicon", "Speedrunning")
                    .build();
            DiscordRPC.discordUpdatePresence(rich);
            SpeedrunTimer.LOGGER.info("updated rich presence");
            //System.out.println("yes");
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        RegistryKey<World> dimension = event.getPlayer().world.getDimensionKey();
        if(dimension.compareTo(World.OVERWORLD) == 0) {
            SpeedrunTimer.LOGGER.info("player logged in, dimension : overworld " );
            actualDimension = "Overworld";
        } else if(dimension.compareTo(World.THE_NETHER) == 0) {
            SpeedrunTimer.LOGGER.info("player logged in, dimension : nether " );
            actualDimension = "Nether";
        } else {
            SpeedrunTimer.LOGGER.info("player logged in, dimension : end " );
            actualDimension = "End";
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        //PlayerEntity player = event.getPlayer();
        //World dimensionFrom = player.world.getServer().getWorld(event.getFrom());
        //World dimensionTo = player.world.getServer().getWorld(event.getTo());
        RegistryKey<World> dimensionFrom = event.getFrom();
        RegistryKey<World> dimensionTo = event.getTo();

        Long currentTimer = new Date().getTime() - startTime;

        if(dimensionTo.compareTo(World.OVERWORLD) == 0) {
            actualDimension = "Overworld";
        } else if(dimensionTo.compareTo(World.THE_NETHER) == 0) {
            actualDimension = "Nether";
        } else {
            actualDimension = "End";
        }

        if( (dimensionFrom.compareTo(World.OVERWORLD)) == 0 && (dimensionTo.compareTo(World.THE_NETHER) == 0) ) {
            steps.put("enterNether", currentTimer);
            DiscordRichPresence rich = new DiscordRichPresence.Builder("Entered Nether at " + Utilities.formatTimer(currentTimer))
                    .setDetails("Dimension : " + actualDimension)
                    .setStartTimestamps(startTime)
                    .setBigImage("mc", "Made with love by Larko")
                    .setSmallImage("clockicon", "Speedrunning")
                    .build();
            DiscordRPC.discordUpdatePresence(rich);
            SpeedrunTimer.LOGGER.info("updated rich presence");
        } else if ( (dimensionFrom.compareTo(World.THE_NETHER)) == 0 && (dimensionTo.compareTo(World.OVERWORLD) == 0) ) {
            steps.put("exitNether", currentTimer);
            DiscordRichPresence rich = new DiscordRichPresence.Builder("Left Nether at " + Utilities.formatTimer(currentTimer))
                    .setDetails("Dimension : " + actualDimension)
                    .setStartTimestamps(startTime)
                    .setBigImage("mc", "Made with love by Larko")
                    .setSmallImage("clockicon", "Speedrunning")
                    .build();
            DiscordRPC.discordUpdatePresence(rich);
            SpeedrunTimer.LOGGER.info("updated rich presence");
        } else if ( (dimensionFrom.compareTo(World.OVERWORLD)) == 0 && (dimensionTo.compareTo(World.THE_END) == 0) ) {
            steps.put("enterEnd", currentTimer);
            DiscordRichPresence rich = new DiscordRichPresence.Builder("Entered End at " + Utilities.formatTimer(currentTimer))
                    .setDetails("Dimension : " + actualDimension)
                    .setStartTimestamps(startTime)
                    .setBigImage("mc", "Made with love by Larko")
                    .setSmallImage("clockicon", "Speedrunning")
                    .build();
            DiscordRPC.discordUpdatePresence(rich);
            SpeedrunTimer.LOGGER.info("updated rich presence");
        }


    }
}
