package moe.nea.ledger

import io.github.notenoughupdates.moulconfig.common.IMinecraft

class ConfigCommand : LedgerCommand() {

	override fun getCommandName(): String {
		return "ledgerconfig"
	}

	override fun processCommand(args: Array<out String>) {
		val editor = Ledger.managedConfig.getEditor()
		editor.search(args.joinToString(" "))
		Ledger.runLater {
			IMinecraft.getInstance().openWrappedScreen(editor)
		}
	}

	override fun getCommandAliases(): List<String> {
		return listOf("moneyledger")
	}
}