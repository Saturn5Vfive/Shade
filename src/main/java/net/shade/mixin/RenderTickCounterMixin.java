package net.shade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.RenderTickCounter;
import net.shade.CmdProsessor;


@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {

	@Shadow
	public float lastFrameDuration;

	@Shadow
	public float tickDelta;

	@Shadow
	private long prevTimeMillis;

	@Shadow
	private float tickTime;

	@Inject(at = @At("HEAD"), method = "beginRenderTick", cancellable = true)
	public void beginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
        if(CmdProsessor.gameFrameRate != 1){
            this.lastFrameDuration = (long_1 - this.prevTimeMillis) / this.tickTime;
		    lastFrameDuration *= CmdProsessor.gameFrameRate;
		    this.prevTimeMillis = long_1;
		    this.tickDelta += this.lastFrameDuration;
        }

	}

}
