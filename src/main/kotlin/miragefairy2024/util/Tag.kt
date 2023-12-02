package miragefairy2024.util

import miragefairy2024.MirageFairy2024DataGenerator
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey

fun Block.registerTagGeneration(tag: TagKey<Block>) {
    MirageFairy2024DataGenerator.blockTagGenerators {
        it(tag).add(this)
    }
}

fun Item.registerTagGeneration(tag: TagKey<Item>) {
    MirageFairy2024DataGenerator.itemTagGenerators {
        it(tag).add(this)
    }
}
