package net.shade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.shade.plugin.ChatPlugin;
import net.shade.CmdProsessor;
import net.shade.plugin.SettingPlugin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.shade.plugin.SettingPlugin;

@Mixin(ClientPlayerEntity.class)
public class ChatOutputMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessageSent(String message, CallbackInfo cb){
        if(message.toLowerCase().startsWith(SettingPlugin.prefix) && SettingPlugin.doChatCommands){
            cb.cancel();
            try{
                CmdProsessor prosessor = new CmdProsessor();
                prosessor.prosess(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
}