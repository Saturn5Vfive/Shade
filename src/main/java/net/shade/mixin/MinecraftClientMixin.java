package net.shade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.shade.plugin.ChatPlugin;
import net.shade.CmdProsessor;
import net.shade.plugin.SettingPlugin;
import net.shade.CmdProsessor;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import net.shade.MixinProsessHandler;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.shade.Shade;
import net.shade.plugin.CommandGuiPlugin;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    private static boolean isFirstGameTick = true;

    @Inject(method = "tick", at = @At("HEAD"))
    public void hookTickEvent(CallbackInfo callbackInfo) {
        MixinProsessHandler.callUpdate();
        if(Shade.cGuiKey.isPressed()){
            MinecraftClient.getInstance().openScreen(new CommandGuiPlugin());
        }
        if(isFirstGameTick){
            try{
                Path autorun = CmdProsessor.shadeFolder.resolve("AutoRun.txt");
                String j = CmdProsessor.readFromInputStream(autorun);
                MinecraftClient.getInstance().player.sendChatMessage(j);
                isFirstGameTick = false;
            }catch(Exception e){}
        }
    }

    @Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 0)}, method = {"doAttack()V"}, cancellable = true)
	private void onDoAttack(CallbackInfo ci)
	{
        MixinProsessHandler.doClicks();
	}

    @Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", ordinal = 0)}, method = {"doItemUse()V"}, cancellable = true)
	private void onDoItemUse(CallbackInfo ci)
	{
        MixinProsessHandler.doRClicks();
	}

}