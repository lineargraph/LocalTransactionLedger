package moe.nea.ledger.events

import com.google.gson.JsonElement
import net.minecraft.client.gui.screens.Screen

class ScreenOpenEvent(val screen: Screen) : LedgerEvent() {
	override fun serialize(): JsonElement {
		TODO("Not yet implemented")
	}
}