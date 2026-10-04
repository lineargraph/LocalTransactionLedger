package moe.nea.ledger

import moe.nea.ledger.events.TriggerEvent
import moe.nea.ledger.utils.di.Inject
import net.minecraft.network.chat.ClickEvent

class TriggerCommand : LedgerCommand() {
	fun getTriggerCommandLine(trigger: String): ClickEvent {
		return ClickEvent.RunCommand("/${getCommandName()} $trigger")
	}
	@Inject
	lateinit var logger: LedgerLogger

	override fun getCommandName(): String {
		return "__ledgertrigger"
	}

	override fun processCommand(args: Array<out String>) {
		val event = TriggerEvent(args.joinToString(" "))
		event.post()
		if (!event.isConsumed)
			logger.printOut("§cCould not find the given trigger. This is an internal command for ledger.")
	}
}