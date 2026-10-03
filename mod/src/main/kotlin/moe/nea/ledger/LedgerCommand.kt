package moe.nea.ledger

abstract class LedgerCommand {
	abstract fun getCommandName(): String
	abstract fun processCommand(args: Array<out String>)
	open fun getCommandAliases(): List<String> = listOf()
	// TODO: add command registration back in
}