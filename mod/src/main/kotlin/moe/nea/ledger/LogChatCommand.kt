package moe.nea.ledger

import moe.nea.ledger.utils.di.Inject
import net.minecraft.command.LedgerCommand
import net.minecraft.command.ICommandSender

class LogChatCommand : LedgerCommand() {
	@Inject
	lateinit var logger: LedgerLogger

	override fun getCommandName(): String {
		return "ledgerlogchat"
	}

	override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
		return true
	}

	override fun getCommandUsage(sender: ICommandSender?): String {
		return ""
	}

	override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
		logger.shouldLog = !logger.shouldLog
		logger.printOut("§eLedger logging toggled " + (if (logger.shouldLog) "§aon" else "§coff") + "§e.")
	}
}