package moe.nea.ledger.mixin;

import moe.nea.ledger.events.BeforeGuiAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenInteractionEvents {
	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void onBeforeKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		new BeforeGuiAction((Screen) (Object) this).post();
	}
// TODO:	@Inject(method = "mouseClicks")

}
