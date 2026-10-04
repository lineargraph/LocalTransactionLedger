package moe.nea.ledger.events

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.ledger.unformattedString
import net.minecraft.network.chat.Component
import java.time.Instant

data class ChatReceived(
	val message: String,
	val timestamp: Instant = Instant.now()
) : LedgerEvent() {
	constructor(event: Component) : this(
		event.string.unformattedString().trimEnd()
	)

	override fun serialize(): JsonElement {
		val json = JsonObject()
		json.addProperty("message", message)
		json.addProperty("timestamp", timestamp.epochSecond)
		return json
	}
}