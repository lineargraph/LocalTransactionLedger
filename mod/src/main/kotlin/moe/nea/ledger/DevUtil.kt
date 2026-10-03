package moe.nea.ledger

import net.fabricmc.loader.api.FabricLoader

object DevUtil {
	val isDevEnv = FabricLoader.getInstance().isDevelopmentEnvironment
}