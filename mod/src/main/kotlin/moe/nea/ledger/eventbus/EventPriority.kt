package moe.nea.ledger.eventbus

import moe.nea.ledger.gen.BuildConfig
import net.fabricmc.fabric.api.event.Event
import net.minecraft.resources.Identifier

enum class EventPriority(
	val identifier: Identifier
) {
	HIGHEST(Identifier.fromNamespaceAndPath(BuildConfig.MODID, "highest")),
	HIGH(Identifier.fromNamespaceAndPath(BuildConfig.MODID, "high")),
	NORMAL(Event.DEFAULT_PHASE),
	LOW(Identifier.fromNamespaceAndPath(BuildConfig.MODID, "low")),
	LOWEST(Identifier.fromNamespaceAndPath(BuildConfig.MODID, "lowest"));
}