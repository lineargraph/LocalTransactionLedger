package moe.nea.ledger

import moe.nea.ledger.events.SupplyDebugInfo
import moe.nea.ledger.utils.di.Inject

class DebugDataCommand : LedgerCommand() {
	override fun getCommandName(): String {
		return "ledgerdebug"
	}

	@Inject
	lateinit var logger: LedgerLogger

	override fun processCommand(args: Array<out String>) {
		val debugInfo = SupplyDebugInfo()
		debugInfo.post()
		logger.printOut("Collected debug info:")
		debugInfo.data.forEach {
			logger.printOut("${it.first}: ${it.second}")
		}
	}
}