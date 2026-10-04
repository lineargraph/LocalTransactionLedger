package moe.nea.ledger

import net.minecraft.network.chat.*
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.regex.Matcher
import java.util.regex.Pattern

// language=regexp
val SHORT_NUMBER_PATTERN = "[0-9]+(?:,[0-9]+)*(?:\\.[0-9]+)?[kKmMbB]?"

// language=regexp
val ROMAN_NUMBER_PATTERN = "[IVXLCDM]+"

val romanNumbers = mapOf(
	'I' to 1,
	'V' to 5,
	'X' to 10,
	'L' to 50,
	'C' to 100,
	'D' to 500,
	'M' to 1000
)

fun parseRomanNumber(string: String): Int {
	var smallestSeenSoFar = Int.MAX_VALUE
	var lastSeenOfSmallest = 0
	var amount = 0
	for (c in string) {
		val cV = romanNumbers[c]!!
		if (cV == smallestSeenSoFar) {
			lastSeenOfSmallest++
			amount += cV
		} else if (cV < smallestSeenSoFar) {
			smallestSeenSoFar = cV
			amount += cV
			lastSeenOfSmallest = 1
		} else {
			amount -= lastSeenOfSmallest * smallestSeenSoFar * 2
			smallestSeenSoFar = cV
			amount += cV
			lastSeenOfSmallest = 1
		}
	}
	return amount
}

val siScalars = mapOf(
	'k' to 1_000.0,
	'K' to 1_000.0,
	'm' to 1_000_000.0,
	'M' to 1_000_000.0,
	'b' to 1_000_000_000.0,
	'B' to 1_000_000_000.0,
)

fun parseShortNumber(string: String): Double {
	var k = string.replace(",", "")
	val scalar = k.last()
	var scalarMultiplier = siScalars[scalar]
	if (scalarMultiplier == null) {
		scalarMultiplier = 1.0
	} else {
		k = k.dropLast(1)
	}
	return k.toDouble() * scalarMultiplier
}

fun Pattern.matches(string: String): Boolean = matcher(string).matches()
inline fun <T> Pattern.useMatcher(string: String, block: Matcher.() -> T): T? =
	matcher(string).takeIf { it.matches() }?.let(block)

fun <T> String.ifDropLast(suffix: String, block: (String) -> T): T? {
	if (endsWith(suffix)) {
		return block(dropLast(suffix.length))
	}
	return null
}

fun String.unformattedString(): String = replace("§.".toRegex(), "")

val timeFormat: DateTimeFormatter = DateTimeFormatterBuilder()
	.appendValue(ChronoField.DAY_OF_MONTH, 2)
	.appendLiteral(".")
	.appendValue(ChronoField.MONTH_OF_YEAR, 2)
	.appendLiteral(".")
	.appendValue(ChronoField.YEAR, 4)
	.appendLiteral(" ")
	.appendValue(ChronoField.HOUR_OF_DAY, 2)
	.appendLiteral(":")
	.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
	.appendLiteral(":")
	.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
	.toFormatter()

fun Instant.formatChat(): Component {
	val text = Component.literal(
		LocalDateTime.ofInstant(this, ZoneId.systemDefault()).format(timeFormat)
	)
	text.style = Style.EMPTY
		.withClickEvent(
			ClickEvent.OpenUrl(URI("https://time.is/${this.epochSecond}"))
		)
		.withHoverEvent(
			HoverEvent.ShowText(Component.literal("Click to show on time.is"))
		)
		.withColor(TextColor.AQUA)
	return text
}

private val formatChatDirection = run {
	fun ItemChange.ChangeDirection.formatChat0(): Component {
		val (text, color) = when (this) {
			ItemChange.ChangeDirection.GAINED -> "+" to TextColor.GREEN
			ItemChange.ChangeDirection.TRANSFORM -> "~" to TextColor.YELLOW
			ItemChange.ChangeDirection.SYNC -> "=" to TextColor.BLUE
			ItemChange.ChangeDirection.CATALYST -> "*" to TextColor.DARK_PURPLE
			ItemChange.ChangeDirection.LOST -> "-" to TextColor.RED
		}
		return Component.literal(text)
			.setStyle(
				Style.EMPTY
					.withColor(color)
					.withHoverEvent(
						HoverEvent.ShowText(
							Component.literal(name).setStyle(Style.EMPTY.withColor(color))
						)
					)
			)
	}
	ItemChange.ChangeDirection.entries.associateWith { it.formatChat0() }
}

fun ItemChange.ChangeDirection.formatChat(): Component {
	return formatChatDirection[this]!!
}

fun ItemChange.formatChat(): Component {
	return Component.literal(" ")
		.append(direction.formatChat())
		.append(" ")
		.append(Component.literal("$count").setStyle(Style.EMPTY.withColor(TextColor.WHITE)))
		.append(Component.literal("x").setStyle(Style.EMPTY.withColor(TextColor.DARK_GRAY)))
		.append(" ")
		.append(
			Component.literal(itemId.string).setStyle(
				Style.EMPTY.withColor(
					TextColor.WHITE
				)
			)
		)
}

