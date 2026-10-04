package moe.nea.ledger.modules

import java.time.Instant
import moe.nea.ledger.ItemChange
import moe.nea.ledger.LedgerEntry
import moe.nea.ledger.LedgerLogger
import moe.nea.ledger.TransactionType
import moe.nea.ledger.eventbus.SubscribeEvent
import moe.nea.ledger.events.GuiClickEvent
import moe.nea.ledger.getDisplayNameU
import moe.nea.ledger.getInternalId
import moe.nea.ledger.matches
import moe.nea.ledger.utils.MigrationUtil.stack
import moe.nea.ledger.utils.di.Inject
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.DyeColor

class ForgeDetection {
	val furnaceSlot = 9 + 4
	val furnaceName = "Forge Slot.*".toPattern()

	@SubscribeEvent
	fun onClick(event: GuiClickEvent) {
		val slot = event.slotIn ?: return
		val chest = (event.screen as? ContainerScreen)?.menu?.container ?: return
		val clickedItem = slot.stack ?: return
		val dyeColor = clickedItem.get(DataComponents.DYE) ?: return
		if (clickedItem.getDisplayNameU() != "Confirm") return
		if (dyeColor == DyeColor.RED) return
		val furnaceSlotName = chest.getItem(furnaceSlot).getDisplayNameU()
		if (!furnaceName.matches(furnaceSlotName))
			return
		val cl = (0 until chest.containerSize - 9)
			.mapNotNull {
				val stack = chest.getItem(it)
				val x = it % 9
				if (x == 4) return@mapNotNull null
				ItemChange(
					stack.getInternalId() ?: return@mapNotNull null,
					stack.count.toDouble(),
					if (x < 4) ItemChange.ChangeDirection.LOST else ItemChange.ChangeDirection.GAINED
				)
			}
		logger.logEntry(
			LedgerEntry(
				TransactionType.FORGED,
				Instant.now(),
				cl,
			)
		)
	}

	@Inject
	lateinit var logger: LedgerLogger

}