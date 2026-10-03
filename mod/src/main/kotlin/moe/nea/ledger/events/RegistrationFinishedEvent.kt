package moe.nea.ledger.events

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class RegistrationFinishedEvent : LedgerEvent() {
	override fun serialize(): JsonElement {
		return JsonPrimitive("RegistrationFinished")
	}
}
