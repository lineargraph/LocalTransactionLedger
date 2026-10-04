package moe.nea.ledger.events

import com.google.gson.JsonElement

class SupplyDebugInfo : LedgerEvent() { // TODO: collect this in the event recorder
	val data = mutableListOf<Pair<String, Any>>()
	fun record(key: String, value: Any) {
		data.add(key to value)
	}

	override fun serialize(): JsonElement {
		TODO("Not yet implemented")
	}
}