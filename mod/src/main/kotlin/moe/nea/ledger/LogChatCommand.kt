package moe.nea.ledger

import moe.nea.ledger.utils.di.Inject

class LogChatCommand : LedgerCommand() {
	@Inject
	lateinit var logger: LedgerLogger

	override fun getCommandName(): String {
		return "ledgerlogchat"
	}

	override fun processCommand(args: Array<out String>) {
		logger.shouldLog = !logger.shouldLog
		logger.printOut("§eLedger logging toggled " + (if (logger.shouldLog) "§aon" else "§coff") + "§e.")
	}
}