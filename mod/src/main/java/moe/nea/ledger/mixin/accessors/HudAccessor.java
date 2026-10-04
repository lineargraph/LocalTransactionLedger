package moe.nea.ledger.mixin.accessors;

import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.PlayerScoreEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Comparator;

@Mixin(Hud.class)
public interface HudAccessor {
	@Accessor("SCORE_DISPLAY_ORDER")
	static Comparator<PlayerScoreEntry> getSCORE_DISPLAY_ORDER$ledger() {
		throw new RuntimeException("Mixin failure");
	}
}
