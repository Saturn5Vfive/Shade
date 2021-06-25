package net.shade;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Clipboard;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import net.shade.plugin.ChatPlugin;
import net.shade.plugin.DiscordWebhook;
import net.shade.plugin.RequirePlugin;
import net.shade.plugin.SettingPlugin;



public class CmdProsessor{

    private String[] args;
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    private String cmom;
    private BlockPos pos;
    private ClientPlayerEntity player = MinecraftClient.getInstance().player;
    private static String xset = "null";
    private static String yset = "null";
    private static String zset = "null";
    private static String nset = "null";
    private static Text component;
    private final Random rand = new Random();
    private static Map <String, String> cc = new HashMap<String, String>(); 
    static boolean doProsessDump = false;
    public static Double gameFrameRate = 1.0;
    private static String text = "";
    public static Path shadeFolder = initfolder();

    public void prosess(String command){ 
        args = command.split(" ");
        pos = new BlockPos(player.getPos());
        cmom = args[0].toLowerCase();
        String ucmom = cmom;
        cmom = cmom.substring(1);


        switch(cmom){


            case "prefix":
            SettingPlugin.prefix = args[1];
            ChatPlugin.sendChat("Updated prefix!");
            break;
        
            case "echo":
            doVariables();
            doFunctions();
            ChatPlugin.sendChat(compileText().trim());
            break;

            case "chat":
            doVariables();
            doFunctions();
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ChatMessageC2SPacket(compileText().trim()));
            break;

            case "run":
            doVariables();
            doFunctions();
            MinecraftClient.getInstance().player.sendChatMessage("/" + compileText().trim());
            if(doProsessDump) nset = "/" + compileText().trim();
            break;

            case "tp":
            doVariables();
            doFunctions();
            try{                    
                Integer x = Integer.valueOf(args[1]);
                Integer y = Integer.valueOf(args[2]);
                Integer z = Integer.valueOf(args[3]);
                player.updatePosition(x + 0.5, y, z + 0.5); 
                ChatPlugin.sendChat("Teleported Player to " + args[1] + " " + args[2] + " " + args[3]);
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Arguments For Command, Use @tp <x> <y> <z>");
            }
            break;

            case "vclip":
            doVariables();
            doFunctions();
            try{
                Integer clipamount = Integer.valueOf(args[1]);
                player.updatePosition(pos.getX(), pos.getY() + clipamount, pos.getZ()); 
                ChatPlugin.sendChat("Clipped Player " + args[1] + " blocks");
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Arguments for Command, Use @vclip <amount>");
            }
            break;

            case "loop":
            doVariables();
            doFunctions();
            try{
                Integer rep = Integer.valueOf(args[1]);
                for(int i = 0; i < rep; i++){
                    String past2 = getPast(2);
                    past2 = past2.replace("%i%", String.valueOf(i));
                    player.sendChatMessage(past2.trim());
                }
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Arguments for Command, Use @loop <repeat> <message>");
            }
            break;

            case "hclip":
            doVariables();
            doFunctions();
            try{
                Integer clipamount = Integer.valueOf(args[1]);
                Vec3d forward = Vec3d.fromPolar(0, player.getYaw()).normalize();  
                player.updatePosition(pos.getX() + forward.x * clipamount, pos.getY(), pos.getZ() + forward.z * clipamount); 
                ChatPlugin.sendChat("Clipped Player " + args[1] + " blocks");
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Arguments for Command, Use @hclip <distance>");
            }
            break;

            case "dump":
            try{
                ItemStack stack = player.getMainHandStack();
                NbtCompound ct = stack.getTag();
                String print = ct == null ? "" : ct.asString();
                ChatPlugin.sendChat("Nbt: " + print);         
                if(doProsessDump) nset = print;           
            }catch(Exception e){

            }
            break;

            case "push":
            doVariables();
            doFunctions();
            try{
                Double force = Double.parseDouble(args[1]);
                Vec3d forward = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
                Vec3d velocity = player.getVelocity();
                MinecraftClient.getInstance().player.setVelocity(velocity.x + forward.x * force, velocity.y + forward.y * force, velocity.z + forward.z * force);
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Arguments for Command, Use @push <force>");
            }
            break;

            case "coords":
            ChatPlugin.sendChat("Player Coords");
            ChatPlugin.sendChat("Player:" + pos.getX() + " " + pos.getY() + " " + pos.getZ());
            if(doProsessDump) nset = pos.getX() + " " + pos.getY() + " " + pos.getZ();
            break;

            default:
                try{
                    if(cc.get(ucmom) != null){
                        if(args.length > 1){
                            nset = args[1];
                        }
                        if(args.length > 2){
                            xset = args[2];
                        }
                        if(args.length > 3){
                            yset = args[3];
                        }
                        if(args.length > 4){
                            zset = args[4];
                        }
                        text = compileText();
                        player.sendChatMessage(cc.get(ucmom));
                    }
                }catch(Exception e){

                }
                if(cc.get(ucmom) == null){
                    ChatPlugin.sendChat("Unknown Command \"" + cmom + "\" use @help to see all valid commands");
                }
            break;

            case "mod":
            doVariables();
            doFunctions();
            ItemStack stack = player.getInventory().getMainHandStack();
            if(stack == null){
                ChatPlugin.sendChat("Hold An Item In your hand");
                break;
            }
            addNbtToStack(stack, args);
            if(doProsessDump) nset = stack.getTag().asString();
            break;
            
            case "help":
            ChatPlugin.sendChat("Commands:");
            ChatPlugin.sendChat(SettingPlugin.prefix + "echo <text>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "chat <text>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "tp <x> <y> <z>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "vclip <distance>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "hclip <distance>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "loop <times> <text>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "coords");
            ChatPlugin.sendChat(SettingPlugin.prefix + "mod <nbt>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "push <force>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "dump");
            ChatPlugin.sendChat(SettingPlugin.prefix + "gamemode <gamemode>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "cvar <variable> <value>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "sudo <packet> <peramiters> (use @sudo help to see arguments)");
            ChatPlugin.sendChat(SettingPlugin.prefix + "exec <commands> (use ; to separate)");
            ChatPlugin.sendChat(SettingPlugin.prefix + "batch <commands> (use :: to separate)");
            ChatPlugin.sendChat(SettingPlugin.prefix + "rotate <pitch> <yaw>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "slot <int>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "set <n/x/y/z> <value> (use %var% to referance a variable, eg:%xvar%)");
            ChatPlugin.sendChat(SettingPlugin.prefix + "give <item> <amount> <nbt>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "hang <millis>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "hook <identifier> <command>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "mixin <ontick/onleftclick/onrightclick/clear> <command>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "if <jumping/sprinting/sneaking/is/nbt/click/midclick/onground/rclick/dropitem/custom> <args1/nbt> <args2> <command> (args1 and 2 only apply for is and nbt)");
            ChatPlugin.sendChat(SettingPlugin.prefix + "fif (same arguments as if)");
            ChatPlugin.sendChat(SettingPlugin.prefix + "velocity <add/set/eset/eadd> <x> <y> <z>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "book <doSign> <nbt>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "place <vector/below>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "varlist");
            ChatPlugin.sendChat(SettingPlugin.prefix + "funclist");
            ChatPlugin.sendChat(SettingPlugin.prefix + "webhook <url> <content>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "tps <number>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "import <url>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "quit");
            ChatPlugin.sendChat(SettingPlugin.prefix + "copy <text>");
            ChatPlugin.sendChat(SettingPlugin.prefix + "load <file>");
            break;

            case "gamemode":
            doVariables();
            doFunctions();
            if(args.length < 2){
                ChatPlugin.sendChat("Incorrect usage, use @gamemode <gamemode>");
                return;
            }
            switch (args[1].toLowerCase()){
                case "creative":
                MinecraftClient.getInstance().interactionManager.setGameMode(GameMode.CREATIVE);
                ChatPlugin.sendChat("Set Gamemode to creative");
                break;

                case "survival":
                MinecraftClient.getInstance().interactionManager.setGameMode(GameMode.SURVIVAL);
                ChatPlugin.sendChat("Set Gamemode to survival");
                break;

                default:
                ChatPlugin.sendChat("Unknown gamemode \""+ args[1] +"\" use creative or survival");
                break;
            }
            break;


            case "cvar":
            doVariables();
            doFunctions();
            try{
                switch(args[1].toLowerCase()){
                case "help":
                ChatPlugin.sendChat(SettingPlugin.prefix + "cvar canFly [Boolean]");
                ChatPlugin.sendChat(SettingPlugin.prefix + "cvar canBuild [Boolean]");
                ChatPlugin.sendChat(SettingPlugin.prefix + "cvar isFlying [Boolean]");
                ChatPlugin.sendChat(SettingPlugin.prefix + "cvar canTakeDamage [Boolean]");
                ChatPlugin.sendChat(SettingPlugin.prefix + "cvar flySpeed [Float]");
                ChatPlugin.sendChat(SettingPlugin.prefix + "cvar stepheight [Float]");
                break;

                case "stepheight":
                try{
                    Integer fi = Integer.parseInt(args[2].trim());
                    player.stepHeight = (float) fi;
                    ChatPlugin.sendChat("Updated \"flySpeed\" to " + args[2]);
                }catch(Exception e) {
                    ChatPlugin.sendChat("Unknown Float \"" + args[2] + "\"");
                }
                break;

                case "canfly":
                switch(args[2].toLowerCase()){
                    case "true":                    
                    player.getAbilities().allowFlying = true;
                    ChatPlugin.sendChat("Updated \"allowFlying\" to true");
                    break;

                    case "false":                    
                    player.getAbilities().allowFlying = false;
                    ChatPlugin.sendChat("Updated \"allowFlying\" to false");
                    break;

                    default:  
                    ChatPlugin.sendChat("Unknown boolean \"" + args[2] + "\"");
                    break;
                }
                break;

                case "canbuild":
                switch(args[2].toLowerCase()){
                    case "true":                    
                    player.getAbilities().allowModifyWorld = true;
                    ChatPlugin.sendChat("Updated \"allowModifyWorld\" to true");
                    break;

                    case "false":                    
                    player.getAbilities().allowModifyWorld = false;
                    ChatPlugin.sendChat("Updated \"allowModifyWorld\" to false");
                    break;

                    default:  
                    ChatPlugin.sendChat("Unknown boolean \"" + args[2] + "\"");
                    break;
                }
                break;

                case "isflying":
                switch(args[2].toLowerCase()){
                    case "true":                    
                    player.getAbilities().flying = true;
                    ChatPlugin.sendChat("Updated \"flying\" to true");
                    break;

                    case "false":                    
                    player.getAbilities().flying = false;
                    ChatPlugin.sendChat("Updated \"flying\" to false");
                    break;

                    default:  
                    ChatPlugin.sendChat("Unknown boolean \"" + args[2] + "\"");
                    break;
                }
                break;

                case "cantakedamage":
                switch(args[2].toLowerCase()){
                    case "true":                    
                    player.getAbilities().invulnerable = true;
                    ChatPlugin.sendChat("Updated \"invulnerable\" to true");
                    break;

                    case "false":                    
                    player.getAbilities().invulnerable = false;
                    ChatPlugin.sendChat("Updated \"invulnerable\" to false");
                    break;

                    default:                    
                    ChatPlugin.sendChat("Unknown boolean \"" + args[2] + "\"");
                    break;
                }
                break;
                
                case "flySpeed":
                try{
                    Integer fi = Integer.parseInt(args[2].trim());
                    MC.player.flyingSpeed = (float) fi;
                    ChatPlugin.sendChat("Updated \"flySpeed\" to " + args[2]);
                }catch(Exception e) {
                    ChatPlugin.sendChat("Unknown Double \"" + args[2] + "\"");
                }
                break;

                default:
                ChatPlugin.sendChat("Invalid Arguments for Command, Use @cvar help");
                break;
            }
            }catch (Exception e){
                ChatPlugin.sendChat("Invalid Arguments for Command, Use @cvar help");
            }
            break;


            case "sudo":
            doVariables();
            doFunctions();
            switch(args[1].toLowerCase()){

                case "help":
                    ChatPlugin.sendChat("Playermovec2spacket\n <Double>x <Double>y <Double>z <boolean>onGround\n PlayerLookc2spacket\n <Float>Yaw <Float>Pitch <boolean>onGround\n Playeractionc2spacket\n <Mode>\n Start_destroy_block\n Stop_destroy_block\n Drop_all_items\n Drop_item\n swap_item_with_offhand\n Clientcommandc2sPacket\n <Mode>\n Start_sneaking\n Stop_sneaking\n Playerinputc2spacket\n <Float>side <Float>forward <boolean>isJumping <Boolean>isSneaking\n");
                break;

                case "playermovec2spacket":
                try{
                    if(args.length < 6) return;
                    Float x = Float.parseFloat(args[2]);
                    Float y = Float.parseFloat(args[3]);
                    Float z = Float.parseFloat(args[4]);
                    Boolean og = Boolean.valueOf(args[5]);
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, og)); 
                }catch(Exception e){
                    ChatPlugin.sendChat("Invalid format");
                }
                break;

                case "playerlookc2spacket":
                try{
                    Float y = Float.parseFloat(args[2]);
                    Float p = Float.parseFloat(args[3]);
                    Boolean og = Boolean.valueOf(args[4]);
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(y, p, og)); 
                }catch(Exception e){
                    ChatPlugin.sendChat("Invalid format");
                }
                break;

                case "playeractionc2spacket":
                try{
                    String playeraction = String.valueOf(args[2].trim());
                    switch(playeraction.toLowerCase()){
                        case "start_destroy_block":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, new BlockPos(0, 0, 0) , Direction.UP));
                        break;

                        case "stop_destroy_block":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(0, 0, 0) , Direction.UP));
                        break;

                        case "drop_all_items":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, new BlockPos(0, 0, 0) , Direction.UP));
                        break;

                        case "drop_item":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ITEM, new BlockPos(0, 0, 0) , Direction.UP));
                        break;

                        case "swap_item_with_offhand":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0) , Direction.UP));
                        break;

                    }                        

                }catch(Exception e){
                    ChatPlugin.sendChat("Invalid format");
                }
                break;

                case "clientcommandc2spacket":
                try{
                    String playermode = String.valueOf(args[2]);
                    switch(playermode.toLowerCase()){

                        case "start_sneaking":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ClientCommandC2SPacket(player, Mode.PRESS_SHIFT_KEY));
                        break;

                        case "stop_sneaking":
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ClientCommandC2SPacket(player, Mode.RELEASE_SHIFT_KEY));
                        break;
                    }
                }catch(Exception e){
                    ChatPlugin.sendChat("Invalid format");
                }
                break;

                case "playerinputc2spacket":
                    try{
                        Float side = Float.parseFloat(args[2]);
                        Float forward = Float.parseFloat(args[3]);
                        Boolean jump = Boolean.valueOf(args[4]);
                        Boolean sneak = Boolean.valueOf(args[5]);
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerInputC2SPacket(side, forward, jump, sneak));
                    }catch(Exception e){
                        ChatPlugin.sendChat("PlayerInputC2SPacket Takes format \"float float boolean boolean\"");
                    }
                break;

                case "playerinteractc2spacket":
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                break;

                default:
                ChatPlugin.sendChat("Unknown Packet identifier");
                break;
            }
            break;

            case "exec":
            String[] cds = compileText().split(";");
            for(int i = 0; i < cds.length; i++){
                player.sendChatMessage(cds[i].trim());
            }
            break;

            case "rotate":
            doVariables();
            doFunctions();
            try{
                player.setPitch(Float.parseFloat(args[1]));
                player.setYaw(Float.parseFloat(args[2]));
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Rotations, use @rotate <pitch> <yaw>");
            }
            break;


            case "slot":
            doVariables();
            doFunctions();
            try{
                Integer slot = Integer.parseInt(args[1]);
                player.getInventory().selectedSlot = slot;
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid slot, use @slot <integer>");
            }
            break;

            case "velocity":
            doVariables();
            doFunctions();
            Double dx = 0.0;
            Double dy = 0.0;
            Double dz = 0.0;
            try{
                dx = Double.parseDouble(args[2]);
                dy = Double.parseDouble(args[3]);
                dz = Double.parseDouble(args[4]);
                switch(args[1]){
                    case "add":
                    player.addVelocity(dx, dy, dz);
                    break;
    
                    case "set":
                    player.setVelocity(dx, dy, dz);
                    break;
    
                    case "eset":
                    Entity vehicle = MC.player.getVehicle();
                    vehicle.setVelocity(dx, dy, dz);
                    break;
    
                    case "eadd":
                    Entity evehicle = MC.player.getVehicle();
                    evehicle.addVelocity(dx, dy, dz);
                    break;
                }
            }catch(Exception e){
                ChatPlugin.sendChat("Incorrect Syntax, use @velocity <add/set/eset/eadd> <x> <y> <z>");
            }

            break;

            case "set":
            doVariables();
            doFunctions();
            try{
                switch(args[1]){
                    case "x":
                    xset = getPast(2).trim();
                    break;

                    case "y":
                    yset = getPast(2).trim();
                    break;

                    case "z":
                    zset = getPast(2).trim();
                    break;

                    case "n":
                    nset = getPast(2).trim();
                    break;
                }

            }catch(Exception e){
                ChatPlugin.sendChat("Please use @set <x/y/z/n> <value>");
            }
            break;

            case "give":
            doVariables();
            doFunctions();
            try{
                Item item = getItem(args[1]);
                Integer amount = Integer.parseInt(args[2]);
                ItemStack staack = new ItemStack(item, amount);
                staack.setTag(parseNBT(getPast(3)));
                setHandToStack(staack);
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Stack Amount");
            }
            break;

            case "hang":
                doVariables();
                doFunctions();
                try{                
                    Integer hangtime = Integer.parseInt(args[1]);
                    Thread.sleep(hangtime);
                }catch(Exception e){
                    ChatPlugin.sendChat("Invalid value, use @hang <millis>");
                }
            break;

            case "hook":
                try{
                    cc.put(args[1].trim(), getPast(2).trim());
                    ChatPlugin.sendChat("registered new hook statement \"" + args[1].trim() + "\"");
                }catch(Exception e){
                    ChatPlugin.sendChat("incorrect hook statement");
                }
            break;

            case "batch":
            String[] ttt = compileText().split("::");
            for(int i = 0; i < ttt.length; i++){
                player.sendChatMessage(ttt[i].trim());
            }
            break;

            case "clearhooks":
            cc.clear();
            ChatPlugin.sendChat("Cleared all Hooks!");
            break;

            case "if":
            parseFew();
            doFunctions();
            switch(args[1].toLowerCase()){

                case "sneaking":
                if(MC.options.keySneak.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "sprinting":
                if(MC.options.keySprint.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "jumping":
                if(MC.options.keyJump.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "onground":
                if(MC.player.isOnGround()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "dropitem":
                if(MC.options.keyDrop.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "click":
                if(MC.options.keyAttack.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "rclick":
                if(MC.options.keyUse.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "midclick":
                if(MC.options.keyPickItem.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "is":
                if(args[2].trim().equals(args[3].trim())){
                    ChatPlugin.sendIngame(getPast(4).trim());
                }
                break;

                case "nbt":
                String tag = MC.player.getMainHandStack().getTag().asString();
                if(tag.contains(args[2])){
                    ChatPlugin.sendIngame(getPast(3).trim());
                }
                break;

                case "custom":
                if(Shade.customKey.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;
            }
            break;

            case "fif":
            parseFew();
            doFunctions();
            switch(args[1].toLowerCase()){

                case "sneaking":
                if(!MC.options.keySneak.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "sprinting":
                if(!MC.options.keySprint.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "jumping":
                if(!MC.options.keyJump.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "onground":
                if(!MC.player.isOnGround()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "dropitem":
                if(!MC.options.keyDrop.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "click":
                if(!MC.options.keyAttack.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "rclick":
                if(!MC.options.keyUse.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "midclick":
                if(!MC.options.keyPickItem.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;

                case "is":
                if(!args[2].trim().equals(args[3].trim())){
                    ChatPlugin.sendIngame(getPast(4).trim());
                }
                break;
                
                case "nbt":
                String tag = MC.player.getMainHandStack().getTag().asString();
                if(!args[2].trim().equals(tag)){
                    ChatPlugin.sendIngame(getPast(3).trim());
                }
                break;

                case "custom":
                if(!Shade.customKey.isPressed()){
                    ChatPlugin.sendIngame(getPast(2).trim());
                }
                break;
            }
            break;

            case "book":
            try{
                doVariables();
                doFunctions();
                ItemStack bstack = player.getMainHandStack();
                String nbt = getPast(2);
                nbt = nbt.replace("$", "\u00a7");
                bstack.setTag(StringNbtReader.parse(nbt));
                Boolean dosign = Boolean.valueOf(args[1]);
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new BookUpdateC2SPacket(bstack, dosign, player.getInventory().selectedSlot));
            }catch(Exception e){
                ChatPlugin.sendChat("Incorrect syntax, use @book <doSign> <text>");
            }
            break;


            case "loadscript":
            NbtCompound nbt = MC.player.getMainHandStack().getTag();
            if(nbt.getString("localscript") != null){
                if(!nbt.getString("localscript").startsWith("@")) return;
                ChatPlugin.sendIngame(nbt.getString("localscript"));
            }
            break;

            case "varlist":
            ChatPlugin.sendChat("Variables are values in the game accessable when running any command use %s<varname>% to make it run in the 2nd cycle");
            ChatPlugin.sendChat("%player%, Name of your player");
            ChatPlugin.sendChat("%var%, first variable");
            ChatPlugin.sendChat("%xvar%, 2nd variable");
            ChatPlugin.sendChat("%yvar%, 3rd variable ");
            ChatPlugin.sendChat("%zvar%, 4th variable");
            ChatPlugin.sendChat("%random%, random integer between 0 and 1000");
            ChatPlugin.sendChat("%plx%, players x coord");
            ChatPlugin.sendChat("%ply%, player y coord");
            ChatPlugin.sendChat("%plz%, player z coord");
            ChatPlugin.sendChat("%hrx%, x of the looking block");
            ChatPlugin.sendChat("%hry%, y of the looking block");
            ChatPlugin.sendChat("%hrz%, z of the looking block");
            ChatPlugin.sendChat("%total%, all text in the command");
            ChatPlugin.sendChat("%pvx%, Players X velocity");
            ChatPlugin.sendChat("%pvy%, Players Y velocity");
            ChatPlugin.sendChat("%pvz%, Players Z velocity");
            break;

            case "funclist":
            ChatPlugin.sendChat("Functions are variables with peramiters, use {!!function} to make it run on the 2nd cycle");
            ChatPlugin.sendChat("{!math}:<Switch:operator>:<Integer:number1>:<Integer:number2> | used to perform math, operator can be *, /, -, or +");
            ChatPlugin.sendChat("{!random}:<Integer:upperlimit>");
            ChatPlugin.sendChat("{!data}:<String:playername>:<hand/x/y/z>");
            ChatPlugin.sendChat("{!string}:<String:tag>, used to get the value of a string nbt tag from an item");
            ChatPlugin.sendChat("{!replace}:<String:total>:<String:selector>:<String:value>, looks in total and replaces selector with value");
            break;

            case "place":
            doVariables();
            doFunctions();
            switch(args[1]){
                case "vector":
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) MC.crosshairTarget));
                break;

                case "below":
                BlockPos pos = new BlockPos(player.getX(), player.getY()-1, player.getZ());
                BlockHitResult hr = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, pos, false);
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hr));
                break;

                case "coords":
                if(args.length < 5){
                    ChatPlugin.sendChat("Incorrect syntax, use @place coords <x> <y> <z>");
                    return;
                }
                int x = Integer.parseInt(args[2]);
                int y = Integer.parseInt(args[3]);
                int z = Integer.parseInt(args[4]);

                BlockPos ppp = new BlockPos(x, y, z);
                BlockHitResult hrr = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, ppp, false);
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hrr));
                break;

                default:
                ChatPlugin.sendChat("Incorrect syntax, use @place <vector/below/coords>");
            }
            break;

            case "webhook":
            try{
                doVariables();
                doFunctions();
                DiscordWebhook whook = new DiscordWebhook(args[1]);
                String content = getPast(2);
                content = content.replace("\"", "\\\"");
                whook.setContent(content);
                whook.setUsername("Shade");
                whook.setAvatarUrl("https://cdn.discordapp.com/attachments/753041003945918547/837560600644943872/shadelogo.png");
                whook.execute();
            }catch(Exception e){
                ChatPlugin.sendChat("Invalid Webhook request!");
            }
            break;

            case "tps":
            try{
                doVariables();
                doFunctions();
                Double g = Double.parseDouble(args[1]);
                g = g / 20;
                gameFrameRate = g;
            }catch(Exception e){

            }
            break;

            case "import":
            if(MC.player != null && args.length > 1){
                MC.player.sendChatMessage(RequirePlugin.require(args[1]));
            }else{
                ChatPlugin.sendChat("Invalid Request");
            }
            break;

            case "quit":
                MC.world.disconnect();
            break;
            
            case "noserver":
                SettingPlugin.doServerControl = false;
                ChatPlugin.sendChat("Disabled Server Chat Control!");
            break;
            
            case "server":
                SettingPlugin.doServerControl = true;
                ChatPlugin.sendChat("Enabled Server Chat Control!");
            break;

            case "mixin":
                if(args.length < 2){
                    return;
                }
                switch(args[1].toLowerCase()){
                    case "ontick":
                        MixinProsessHandler.addOnTick(getPast(2).trim());
                    break;


                    case "onleftclick":
                        MixinProsessHandler.addOnLeftClick(getPast(2).trim());
                    break;

                    case "onrightclick":
                        MixinProsessHandler.addOnRightClick(getPast(2).trim());
                    break;

                    case "clear":
                        MixinProsessHandler.destroyall();
                        ChatPlugin.sendChat("Cleared all events");
                    break;
                }
            break;

            case "tick":
            MixinProsessHandler.addOnTick(getPast(1).trim());
            break;

            case "copy":
                if(args.length < 2){
                    return;
                }
                new Clipboard().setClipboard(MinecraftClient.getInstance().getWindow().getHandle(), getPast(1).trim());
            break;

            case "load":
                Path myfile = shadeFolder.resolve(args[1]);
                String j = readFromInputStream(myfile);
                MC.player.sendChatMessage(j);
            break;
        }
    }

    public String getPast(int past){
        String ttext = "";
        for(int i = past; i < args.length; i++){
            ttext = ttext + " " + args[i]; 
        }
        return ttext;
    }

    private void addNbtToStack(ItemStack stack, String[] args)
	{
		String nbt = compileText();
		nbt = nbt.replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
		
		if(!stack.hasTag())
			stack.setTag(new NbtCompound());
		
		try
		{
			NbtCompound tag = StringNbtReader.parse(nbt);
			stack.getTag().copyFrom(tag);
            ChatPlugin.sendChat("Modified NBT of Held item");
			
		}catch(Exception e)
		{
			ChatPlugin.sendChat(e.getMessage());
            ChatPlugin.sendChat("Nbt data is invalid");
		}
	}

    private NbtCompound parseNBT(String nbt){
        try{
            return StringNbtReader.parse(nbt);
        }catch(Exception e){
            ChatPlugin.sendChat(e.getMessage());
            ChatPlugin.sendChat("NBT data is invalid.");
        }
        return null;
    }

    private void setHandToStack(ItemStack stack){
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(36 + player.getInventory().selectedSlot, stack));
    }

    private Item getItem(String id)
	{
		try
		{
			return Registry.ITEM.get(new Identifier(id));
			
		}catch(InvalidIdentifierException e)
		{
			ChatPlugin.sendChat("Invalid Item: " + id);
		}

        return null;
	}

    private static void showClickCommand(String text, String command)
	{
        text = text.replace("&", "\u00a7");
		component = new LiteralText(text);
		
		ClickEvent event = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
		component.getStyle().withClickEvent(event);
        MC.inGameHud.getChatHud().addMessage(component);
	}

    private static void showOpenLink(String text, String url)
	{
        text = text.replace("&", "\u00a7");
		component = new LiteralText(text);
		
		ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
		component.getStyle().withClickEvent(event);
        MC.inGameHud.getChatHud().addMessage(component);
	}

    private void doVariables(){
        BlockHitResult blockHitResult = (BlockHitResult) MC.player.raycast(200, MC.getTickDelta(), true);
        BlockPos dest = new BlockPos(blockHitResult.getBlockPos()).offset(Direction.UP, 1);
        Integer xx = (int) player.getX();
        Integer xy = (int) player.getY();
        Integer xz = (int) player.getZ();
        Integer hx = (int) dest.getX();
        Integer hy = (int) dest.getY();
        Integer hz = (int) dest.getZ();
        Integer ra = rand.nextInt(1000);
        Vec3d v = MC.player.getVelocity();
        Double vx = v.x;
        Double vy = v.y;
        Double vz = v.z;
        HitResult ehr = MC.crosshairTarget;
        for(int i = 0; i < args.length; i++){
            args[i] = args[i].replace("%player%", MinecraftClient.getInstance().getSession().getUsername());
            args[i] = args[i].replace("%yvar%", yset).replace("%zvar%", zset).replace("%xvar%", xset).replace("%var%", nset).replace("%total%", text);
            args[i] = args[i].replace("%random%", ra.toString());
    
            try{
                args[i] = args[i].replace("%loname%", ((EntityHitResult)ehr).getEntity().getEntityName());
            }catch(Exception e){
                args[i] = args[i].replace("%loname%", "null");
            }
    
            args[i] = args[i].replace("%ply%", xy.toString()).replace("%plx%", xx.toString()).replace("%plz%", xz.toString());
            args[i] = args[i].replace("%hry%", hy.toString()).replace("%hrx%", hx.toString()).replace("%hrz%", hz.toString());  
            args[i] = args[i].replace("%pvy%", vy.toString()).replace("%pvx%", vx.toString()).replace("%pvz%", vz.toString());  
        }
    }

    private void doFunctions(){
        for(int i = 0; i < args.length; i++){
            if(args[i].contains("{!math}")){
                String math1 = args[i];
                math1.replace("{!math}", "");
                String[] math = math1.split(":");
                if(math.length < 3){
                    return;
                }
                String output = "0";
                switch(math[1]){
                    case "+":
                    output = String.valueOf((int) (Double.valueOf(math[2]) + Double.valueOf(math[3])));
                    break;

                    case "-":
                    output = String.valueOf((int) (Double.valueOf(math[2]) - Double.valueOf(math[3])));
                    break;

                    case "*":
                    output = String.valueOf((int) (Double.valueOf(math[2]) * Double.valueOf(math[3])));
                    break;

                    case "/":
                    output = String.valueOf((int) (Double.valueOf(math[2]) / Double.valueOf(math[3])));
                    break;
                }
                args[i] = output;
            }
            if(args[i].contains("{!random}")){
                String math1 = args[i];
                math1.replace("{!random}", "");
                Random rand = new Random();
                String[] math = math1.split(":");
                if(math.length < 2){
                    return;
                }
                String output = String.valueOf(rand.nextInt(Integer.valueOf(math[1])));
                args[i] = output;
            }
            if(args[i].contains("{!string}")){
                String e = args[i];
                e = e.replace("{!string}", "");
                String[] g = e.split(":");
                if(g.length < 1){
                    return;
                }
                if(g[1] == null){
                    args[i] = "null";
                    return;
                }
                String n = "null";
                if(MC.player.getMainHandStack().hasTag()){
                    n = MC.player.getMainHandStack().getTag().getString(g[1]);
                    if(n == null || n == ""){
                        n = "null";
                    }
                }else{
                    args[i] = "null";
                }
                args[i] = n;
            }
            if(args[i].contains("{!data}")){
                String parser = args[i];
                parser = parser.replace("{!data}:", "");
                String[] parsed = parser.split(":");
                switch(parsed[1]){
                    case "hand":
                    AbstractClientPlayerEntity eplayer = getPlayer(parsed[0]);
                    ItemStack item = eplayer.getInventory().getMainHandStack();
                    NbtCompound t = item.getTag();
                    String nbt = t.asString().trim();
                    args[i] = nbt;
                    break;

                    case "x":
                    AbstractClientPlayerEntity xeplayer = getPlayer(parsed[0]);
                    args[i] = String.valueOf((int) xeplayer.getX());
                    break;

                    
                    case "y":
                    AbstractClientPlayerEntity yeplayer = getPlayer(parsed[0]);
                    args[i] = String.valueOf((int) yeplayer.getY());
                    break;

                    
                    case "z":
                    AbstractClientPlayerEntity zeplayer = getPlayer(parsed[0]);
                    args[i] = String.valueOf((int) zeplayer.getZ());
                    break;
                }
            }
        }
    }

    private void doKwarg(){

    }

    private void parseFew(){
        for(int i = 0; i < args.length; i++){
            args[i] = args[i].replace("%player%", MinecraftClient.getInstance().getSession().getUsername());
            args[i] = args[i].replace("%xvar%", xset).replace("%var%", nset);
            args[i] = args[i].replace("%total%", getPast(1));  
        }
    }

    public String compileText(){
        String text = "";
        for(int i = 1; i < args.length; i++){
            text = text + " " + args[i];
        }
        return text;
    }

    //sus
    private AbstractClientPlayerEntity getPlayer(String name)
	{
		for(AbstractClientPlayerEntity player : MC.world.getPlayers())
		{
			if(!player.getEntityName().equalsIgnoreCase(name))
				continue;
			
			return player;
		}
		
		return MC.player;
	}

    private static Path initfolder(){
        Path mcfolder = MC.runDirectory.toPath().normalize();
        Path shadeFolder = mcfolder.resolve("shade");
        try{
            Files.createDirectories(shadeFolder);
        }catch(Exception e){
            e.printStackTrace();
        }
        return shadeFolder;
    }

    public static String readFromInputStream(Path p){
        try{
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br = Files.newBufferedReader(p)) {
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line);
                }
            }
            return resultStringBuilder.toString();
        }catch(Exception e){
            return "";
        }
    }
}