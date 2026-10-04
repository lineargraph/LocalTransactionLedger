package moe.nea.ledger.events

import com.google.gson.JsonElement
import com.google.gson.JsonNull

class LateWorldLoadEvent : LedgerEvent() {
	override fun serialize(): JsonElement {
		return JsonNull.INSTANCE
	}
}
