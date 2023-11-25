package miragefairy2024.util

import net.minecraft.block.ComposterBlock
import net.minecraft.item.Item

fun Item.registerComposterInput(chance: Float) {
    ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(this, chance)
}
