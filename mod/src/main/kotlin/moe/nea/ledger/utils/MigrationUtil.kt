package moe.nea.ledger.utils

import net.minecraft.world.inventory.Slot

object MigrationUtil {
	val Slot.stack get() = item.takeIf { !it.isEmpty }
}