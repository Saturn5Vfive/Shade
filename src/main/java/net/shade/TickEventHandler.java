package net.shade;

import net.shade.plugin.SettingPlugin;
import net.shade.plugin.ChatPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.shade.CmdProsessor;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class TickEventHandler{

    static ClientPlayerEntity player = MinecraftClient.getInstance().player;
    public static ArrayList<String> thooks = new ArrayList<String>();

    public static void subscribe(String statement) {
        thooks.add(statement);
    }
    
    public static void unsubscribeAll(){
        thooks.clear();
    }

    public static void callUpdate(){
        player = MinecraftClient.getInstance().player;
        for (int i = 0; i < thooks.size(); i++) {
            if(player != null){
                ChatPlugin.sendIngame(thooks.get(i));
            }
        }
    }
}