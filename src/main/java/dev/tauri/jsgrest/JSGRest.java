package dev.tauri.jsgrest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Mod(JSGRest.MOD_ID)
public class JSGRest {
    public static final String MOD_ID = "jsg_rest";
    public static final String MOD_NAME = "Just Stargate Mod: Rest API";

    public static String MOD_VERSION = "";
    public static final String MC_VERSION = "1.20.1";
    public static Logger logger;
    public static JSGRest instance;
    public static File clientModPath;

    public JSGRest() {
        instance = this;
        logger = LoggerFactory.getLogger(MOD_NAME);

        ModList.get().getModContainerById(MOD_ID).ifPresentOrElse(container -> {
            MOD_VERSION = MC_VERSION + "-" + container.getModInfo().getVersion().getQualifier();
            clientModPath = container.getModInfo().getOwningFile().getFile().getFilePath().toFile();
        }, () -> {
        });

        logger.info("Started loading JSG:Rest mod in " + clientModPath.getAbsolutePath());
        RestConfig.load();
        RestConfig.register();
        logger.info("Successfully registered Config!");


        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        RestAPI.start();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        RestAPI.stop();
    }
}
