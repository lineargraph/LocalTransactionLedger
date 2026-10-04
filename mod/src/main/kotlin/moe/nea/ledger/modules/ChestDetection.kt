package moe.nea.ledger.modules

import java.time.Instant
import moe.nea.ledger.ItemChange
import moe.nea.ledger.ItemId
import moe.nea.ledger.ItemIdProvider
import moe.nea.ledger.events.GuiClickEvent
import moe.nea.ledger.getDisplayNameU
import moe.nea.ledger.getInternalId
import moe.nea.ledger.getLore
import moe.nea.ledger.utils.MigrationUtil.stack
import moe.nea.ledger.utils.di.Inject
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.item.Items

abstract class ChestDetection {
	data class ChestCost(
		val diff: List<ItemChange>,
		val timestamp: Instant,
	)

	val stainedGlassPanes by lazy { Items.STAINED_GLASS_PANE.asList().toSet() }

	@Inject
	lateinit var itemIdProvider: ItemIdProvider

	fun scrapeChestReward(event: GuiClickEvent): ChestCost? {
		if (!event.screenName.endsWith(" Chest")) return null
		val rewardSlot = event.slotIn ?: return null
		val rewardStack = rewardSlot.stack ?: return null
		val container = (event.screen as? ContainerScreen)?.menu?.container ?: return null
		val name = rewardStack.getDisplayNameU()
		if (name != "§aOpen Reward Chest") return null
		val lore = rewardStack.getLore()
		val cost = itemIdProvider.findCostItemsFromSpan(lore)
		val gain = (9..18)
			.mapNotNull { container.getItem(it) }
			.filter { it.item !in stainedGlassPanes }
			.map {
				it.getInternalId()?.withcount(it.count)
					?: itemIdProvider.findStackableItemByName(it.getDisplayNameU())
					?: ItemId.NIL.withcount(it.count)
			}
		return ChestCost(
			cost.map { ItemChange.lose(it.first, it.second) } + gain.map { ItemChange.gain(it.first, it.second) },
			Instant.now()
		)
	}

}