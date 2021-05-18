package net.shade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.shade.plugin.ChatPlugin;
import net.shade.CmdProsessor;
import net.shade.plugin.SettingPlugin;
import net.shade.TickEventHandler;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.shade.Shade;
import net.shade.plugin.CommandGuiPlugin;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void hookTickEvent(CallbackInfo callbackInfo) {
        TickEventHandler.callUpdate();
        if(Shade.cGuiKey.isPressed()){
            MinecraftClient.getInstance().openScreen(new CommandGuiPlugin());
        }
    }
}