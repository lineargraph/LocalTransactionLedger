package moe.nea.ledger.events

import com.google.gson.JsonElement
import net.minecraft.world.inventory.Slot

data class GuiClickEvent(
	val slotIn: Slot?, val slotId: Int, val clickedButton: Int, val clickType: Int
) : LedgerEvent() {
	override fun serialize(): JsonElement {
		TODO("Not yet implemented")
	}
}