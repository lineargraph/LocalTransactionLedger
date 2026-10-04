package moe.nea.ledger.events

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class TickEvent(val tickEvent: Int) : LedgerEvent() {
	override fun serialize(): JsonElement {
		return JsonPrimitive(tickEvent)
	}
}