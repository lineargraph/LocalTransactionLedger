package moe.nea.ledger.events

import com.google.gson.JsonElement
import moe.nea.ledger.unformattedString
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.Slot

data class GuiClickEvent(
	val screen: Screen,
	val slotIn: Slot?, val slotId: Int, val clickedButton: Int, val clickType: Int
) : LedgerEvent() {
	val screenName = screen.title.getString().unformattedString()
	override fun serialize(): JsonElement {
		TODO("Not yet implemented")
	}
}