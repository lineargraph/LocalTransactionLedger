package moe.nea.ledger.events

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.ledger.telemetry.GuiContextValue
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen

data class BeforeGuiAction(val gui: Screen) : LedgerEvent() {
	val chest = gui as? ContainerScreen
	val chestSlots = chest?.menu?.container
	override fun serialize(): JsonElement {
		return JsonObject().apply {
			add("gui", GuiContextValue(gui).serialize())
		}
	}
}
