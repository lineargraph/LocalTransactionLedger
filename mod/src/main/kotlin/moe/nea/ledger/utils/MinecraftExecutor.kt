package moe.nea.ledger.utils

import moe.nea.ledger.utils.di.Inject
import net.minecraft.client.Minecraft
import java.util.concurrent.Executor

class MinecraftExecutor : Executor {
	@Inject
	lateinit var minecraft: Minecraft
	override fun execute(command: Runnable) {
		minecraft.execute(command)
	}
}