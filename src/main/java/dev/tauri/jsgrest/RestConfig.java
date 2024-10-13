package dev.tauri.jsgrest;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;

public class RestConfig {
    private static final String CONFIG_FILE_NAME = "jsg/rest/";

    /**
     * Builder from JSG
     */
    public static class JSGConfigChild {
        public ForgeConfigSpec.Builder builder;
        public String name;

        public JSGConfigChild setBuilder(ForgeConfigSpec.Builder b) {
            builder = b;
            return this;
        }

        public JSGConfigChild setName(String name) {
            this.name = name;
            return this;
        }

        public JSGConfigChild(ForgeConfigSpec.Builder b, String n) {
            setBuilder(b);
            setName(n);
        }
    }

    private static final ArrayList<JSGConfigChild> LIST = new ArrayList<>();

    public static void register() {
        LIST.clear();
        LIST.add(new JSGConfigChild(General.BUILDER, "General"));

        for (JSGConfigChild child : LIST) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, child.builder.build(), CONFIG_FILE_NAME + child.name + ".toml");
        }
    }

    public static void load() {
    }

    public static class General {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec.IntValue port = BUILDER
                .comment(
                        "Port to listen on"
                ).defineInRange("Port", 7070, 0, 99999);

        public static final ForgeConfigSpec.ConfigValue<String> token = BUILDER
                .comment(
                        "Auth token",
                        "Leave blank for no authentication"
                ).define("Token", "");
    }
}
