package miragefairy2024.util.init

import miragefairy2024.MirageFairy2024DataGenerator
import miragefairy2024.util.concat
import miragefairy2024.util.getIdentifier
import miragefairy2024.util.with
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.Model
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TexturedModel
import net.minecraft.item.Item
import net.minecraft.util.Identifier

fun Item.registerItemModelGeneration(model: Model) {
    MirageFairy2024DataGenerator.itemModelGenerations += {
        it.register(this, model)
    }
}

fun Item.registerGeneratedItemModelGeneration() = this.registerItemModelGeneration(Models.GENERATED)

fun TexturedModel.registerModelGeneration(identifier: Identifier) {
    MirageFairy2024DataGenerator.blockStateModelGenerations += {
        this.model.upload(identifier, this.textures, it.modelCollector)
    }
}

fun Model.registerModelGeneration(identifier: Identifier, vararg textureEntries: Pair<TextureKey, Identifier>) = this.with(*textureEntries).registerModelGeneration(identifier)

fun Block.registerModelGeneration(texturedModel: TexturedModel) {
    MirageFairy2024DataGenerator.blockStateModelGenerations += {
        texturedModel.model.upload("block/" concat this.getIdentifier(), texturedModel.textures, it.modelCollector)
    }
}


fun Block.registerSingletonBlockStateGeneration() {
    MirageFairy2024DataGenerator.blockStateModelGenerations += {
        it.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(this, "block/" concat this.getIdentifier()))
    }
}
