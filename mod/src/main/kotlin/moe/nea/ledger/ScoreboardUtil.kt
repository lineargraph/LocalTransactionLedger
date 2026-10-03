package moe.nea.ledger

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Hud
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerTeam

object ScoreboardUtil {

	fun getScoreboardStrings() =
		getScoreboardEntries().map { it.getString().unformattedString() }

	fun getScoreboardEntries(): List<Component> {
		val scoreboard = Minecraft.getInstance().level?.scoreboard ?: return listOf()
		var objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return listOf()
		val scoreList = scoreboard.listPlayerScores(objective)
			.asSequence()
			.filter { !it.isHidden }
			.sortedWith(Hud.SCORE_DISPLAY_ORDER)
			.take(15)
			.map {
				val team = scoreboard.getPlayerTeam(it.owner())
				val ownerName = it.ownerName()
				PlayerTeam.formatNameForTeam(team, ownerName)
			}
//			.map { stripAlien(it) }
			.toList()
			.reversed()
		return scoreList
	}

//	fun stripAlien(string: String): String {
//		val sb = StringBuilder()
//		for (c in string) {
//			if (Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) > 0 || c == '§')
//				sb.append(c)
//		}
//		return sb.toString()
//	}
}