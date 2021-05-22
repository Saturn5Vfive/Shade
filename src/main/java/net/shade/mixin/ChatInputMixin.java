package net.shade.mixin;



import java.util.List;
import net.shade.plugin.ChatEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.OrderedText;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

@Mixin(ChatHud.class)
public class ChatInputMixin extends DrawableHelper
{
	@Shadow
	private List<ChatHudLine<OrderedText>> visibleMessages;
	
	@Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
	private void onAddMessage(Text eventText, int line, CallbackInfo ci)
	{
        ChatEventHandler.prosessChatEvent(eventText.getString());
	}
}
