package miragefairy2024.util.init

import miragefairy2024.MirageFairy2024DataGenerator
import net.minecraft.data.client.Model
import net.minecraft.data.client.Models
import net.minecraft.item.Item

fun Item.registerItemModelGeneration(model: Model) {
    MirageFairy2024DataGenerator.itemModelGenerations += {
        it.register(this, model)
    }
}

fun Item.registerGeneratedItemModelGeneration() = this.registerItemModelGeneration(Models.GENERATED)
