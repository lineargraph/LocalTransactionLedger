package moe.nea.ledger.events

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.ledger.telemetry.GuiContextValue
import moe.nea.ledger.unformattedString
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.ChestMenu

data class BeforeGuiAction(val gui: Screen) : LedgerEvent() {
	val chest = gui as? ContainerScreen
	val chestSlots = chest?.menu?.container
	val screen get() = gui
	val screenName = screen.title.getString().unformattedString()
	override fun serialize(): JsonElement {
		return JsonObject().apply {
			add("gui", GuiContextValue(gui).serialize())
		}
	}
}
