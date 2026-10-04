package moe.nea.ledger

import java.util.UUID
import net.minecraft.client.Minecraft

object MCUUIDUtil {

	val NIL_UUID = UUID(0L, 0L)
	fun getPlayerUUID(): UUID {
		val currentUUID = Minecraft.getInstance().player?.uuid
			?: Minecraft.getInstance().gameProfile.id
			?: lastKnownUUID
		lastKnownUUID = currentUUID
		return currentUUID
	}


	private var lastKnownUUID: UUID = NIL_UUID

}