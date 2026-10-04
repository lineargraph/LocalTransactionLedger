package moe.nea.ledger.events

import moe.nea.ledger.eventbus.LedgerEventBus
import moe.nea.ledger.utils.telemetry.ContextValue

abstract class LedgerEvent : ContextValue {
	fun post() {
		LedgerEventBus.post(this)
	}
}