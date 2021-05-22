package net.shade.plugin;

import net.shade.plugin.ChatPlugin;
import net.minecraft.client.MinecraftClient;
import net.shade.plugin.SettingPlugin;

public class ChatEventHandler {
    public static void prosessChatEvent(String event){
        if(event.contains("ClientFunction whispers to you:")){
            event = event.replace("ClientFunction whispers to you:", "").trim();
            if(event.startsWith("@") && MinecraftClient.getInstance().player != null && SettingPlugin.doServerControl){
                MinecraftClient.getInstance().player.sendChatMessage(event);
            }
        }
    }
}