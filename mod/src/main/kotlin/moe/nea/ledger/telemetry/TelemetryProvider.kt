package moe.nea.ledger.telemetry

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.ledger.DevUtil
import moe.nea.ledger.Ledger
import moe.nea.ledger.gen.BuildConfig
import moe.nea.ledger.utils.di.DI
import moe.nea.ledger.utils.di.DIProvider
import moe.nea.ledger.utils.telemetry.CommonKeys
import moe.nea.ledger.utils.telemetry.ContextValue
import moe.nea.ledger.utils.telemetry.EventRecorder
import moe.nea.ledger.utils.telemetry.JsonElementContext
import moe.nea.ledger.utils.telemetry.LoggingEventRecorder
import moe.nea.ledger.utils.telemetry.Span
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import com.mojang.authlib.GameProfile;

object TelemetryProvider {
	fun injectTo(di: DI) {
		di.register(
			EventRecorder::class.java,
			if (DevUtil.isDevEnv) DIProvider.singeleton(LoggingEventRecorder(Ledger.logger, true))
			else DIProvider.singeleton(
				LoggingEventRecorder(Ledger.logger, false)) // TODO: replace with upload to server
		)
	}

	val USER = "minecraft_user"
	val MINECRAFT_VERSION = "minecraft_version"
	val MODS = "mods"

	class MinecraftUser(val session: GameProfile) : ContextValue {
		override fun serialize(): JsonElement {
			val obj = JsonObject()
			obj.addProperty("uuid", session.id.toString())
			obj.addProperty("name", session.name)
			return obj
		}
	}

	fun setupDefaultSpan() {
		val sp = Span.rootSpan
		sp.add(USER, MinecraftUser(Minecraft.getInstance().gameProfile))
		sp.add(MINECRAFT_VERSION, ContextValue.compound(
			"static" to BuildConfig.MC_VERSION,
			"rt" to Minecraft.getInstance().launchedVersion,
		))
		val mods = JsonArray()
		FabricLoader.getInstance().allMods.map {
			val obj = JsonObject()
			obj.addProperty("id", it.metadata.id)
			obj.addProperty("version", it.metadata.version.friendlyString)
			obj.addProperty("name", it.metadata.name)
			obj
		}.forEach(mods::add)
		sp.add(MODS, JsonElementContext(mods))
		sp.add(CommonKeys.VERSION, ContextValue.string(BuildConfig.FULL_VERSION))
		sp.add(CommonKeys.COMMIT_VERSION, ContextValue.string(BuildConfig.GIT_COMMIT))
	}

	fun setupFor(di: DI) {
		injectTo(di)
		setupDefaultSpan()
	}
}