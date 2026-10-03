package moe.nea.ledger

import net.minecraft.core.component.DataComponents
import net.minecraft.inventory.IInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import kotlin.jvm.optionals.getOrNull


fun ItemStack.getExtraAttributes(): CompoundTag {
	// TODO: unsafeGetTag
	return this.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return CompoundTag()
}

fun ItemStack?.getInternalId(): ItemId? {
	if (this == null) return null
	val extraAttributes = getExtraAttributes()
	var id = extraAttributes.getString("id").orElse("")
	id = id.takeIf { it.isNotBlank() }
	if (id == "PET") {
		id = getPetId() ?: id
	}
	if (id == "ENCHANTED_BOOK") {
		id = getEnchanments().entries.singleOrNull()?.let {
			"${it.key};${it.value}".uppercase()
		}
	}
	return id?.let(::ItemId)
}

fun ItemStack.getEnchanments(): Map<String, Int> {
	val enchantments = getExtraAttributes().getCompound("enchantments").getOrNull() ?: return mapOf()
	return enchantments.keySet().associateWith { enchantments.getInt(it).orElseThrow() }
}

class PetInfo {
	var type: String? = null
	var tier: String? = null
}

fun ItemStack.getPetId(): String? {
	val petInfoStr = getExtraAttributes().getString("petInfo").getOrNull() ?: return null
	val petInfo = runCatching {
		Ledger.gson.fromJson(
			petInfoStr,
			PetInfo::class.java
		)
	}.getOrNull() // TODO: error reporting to sentry
	if (petInfo?.type == null || petInfo.tier == null) return null
	return petInfo.type + ";" + rarityToIndex(petInfo.tier ?: "")
}

fun rarityToIndex(rarity: String): Int {
	return when (rarity) {
		"COMMON" -> 0
		"UNCOMMON" -> 1
		"RARE" -> 2
		"EPIC" -> 3
		"LEGENDARY" -> 4
		"MYTHIC" -> 5
		else -> -1
	}
}

fun ItemStack.getLore(): List<String> {
	// TODO: non stringified lore handling
	return get(DataComponents.LORE)
		?.lines
		?.map { it.getString().unformattedString() } ?: listOf() // TODO: should this be non stringified? probably not
}


fun IInventory.asIterable(): Iterable<ItemStack?> = object : Iterable<ItemStack?> {
	override fun iterator(): Iterator<ItemStack?> {
		return object : Iterator<ItemStack?> {
			var i = 0
			override fun hasNext(): Boolean {
				return i < this@asIterable.sizeInventory
			}

			override fun next(): ItemStack? {
				if (!hasNext()) throw NoSuchElementException("$i is out of range for inventory ${this@asIterable}")
				return this@asIterable.getStackInSlot(i++)
			}
		}
	}
}

fun ItemStack.getNbtDisplayName(): Component {
	return this.get(DataComponents.CUSTOM_NAME)
		?: this.get(DataComponents.ITEM_NAME)
		?: this.displayName
}

fun ItemStack.getDisplayNameU(): String {
	return getNbtDisplayName().getString().unformattedString()
}

