package moe.nea.ledger.events

import com.google.gson.JsonElement
import moe.nea.ledger.ItemId

class ExtraSupplyIdEvent(
	private val store: (String, ItemId) -> Unit
) : LedgerEvent() {
	fun store(name: String, id: ItemId) {
		store.invoke(name, id)
	}

	override fun serialize(): JsonElement {
		TODO("Not yet implemented")
	}
}
