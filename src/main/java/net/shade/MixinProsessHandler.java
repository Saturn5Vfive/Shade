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
public class MixinProsessHandler{

    static ClientPlayerEntity player = MinecraftClient.getInstance().player;
    public static ArrayList<String> tickevents = new ArrayList<String>();
    public static ArrayList<String> chatevents= new ArrayList<String>();
    public static ArrayList<String> clickevents = new ArrayList<String>();
    public static ArrayList<String> rclickevents = new ArrayList<String>();

    public static void addOnTick(String statement) {
        tickevents.add(statement);
    }

    public static void addOnGotChat(String statement) {
        chatevents.add(statement);
    }

    public static void addOnLeftClick(String statement) {
        clickevents.add(statement);
    }

    public static void addOnRightClick(String statement) {
        rclickevents.add(statement);
    }
    
    public static void destroyall(){
        tickevents.clear();
        clickevents.clear();
        rclickevents.clear();
        chatevents.clear();
    }

    public static void callUpdate(){
        player = MinecraftClient.getInstance().player;
        for (int i = 0; i < tickevents.size(); i++) {
            if(player != null){
                ChatPlugin.sendIngame(tickevents.get(i));
            }
        }
    }

    public static void doClicks(){
        player = MinecraftClient.getInstance().player;
        for (int i = 0; i < clickevents.size(); i++) {
            if(player != null){
                ChatPlugin.sendIngame(clickevents.get(i));
            }
        }
    }

    public static void doRClicks(){
        player = MinecraftClient.getInstance().player;
        for (int i = 0; i < rclickevents.size(); i++) {
            if(player != null){
                ChatPlugin.sendIngame(rclickevents.get(i));
            }
        }
    }

    public static void doGotChat(String gotchat){
        player = MinecraftClient.getInstance().player;
        for (int i = 0; i < chatevents.size(); i++) {
            if(player != null){
                ChatPlugin.sendIngame(chatevents.get(i));
            }
        }
    }
}