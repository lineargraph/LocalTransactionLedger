package moe.nea.ledger.events

import com.google.gson.JsonElement

data class TriggerEvent(
	val action: String,
	var isConsumed: Boolean = false
	// TODO: make the consumption do just about anything
) : LedgerEvent() {

	override fun serialize(): JsonElement {
		TODO("Not yet implemented")
	}
}
