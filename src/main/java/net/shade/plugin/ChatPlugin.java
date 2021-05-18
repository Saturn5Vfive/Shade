package net.shade.plugin;

import net.shade.Shade;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.text.LiteralText;

public class ChatPlugin {
    public static void sendChat(String message){
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.DARK_GRAY + "[" + Formatting.GRAY + "Shade" + Formatting.DARK_GRAY + "] " + Formatting.RESET + message), false);
    }
    public static void sendHud(String message){
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.DARK_GRAY + "[" + Formatting.GRAY + "Shade" + Formatting.DARK_GRAY + "] " +  Formatting.RESET + message), true);
    }
    public static void sendIngame(String message){
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendChatMessage(message);
    }
    public void doLiteralText(Text component)
	{
		ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
		chatHud.addMessage(component);
	}
}