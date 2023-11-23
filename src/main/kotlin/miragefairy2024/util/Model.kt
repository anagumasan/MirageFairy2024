package miragefairy2024.util

import com.google.gson.JsonElement
import miragefairy2024.MirageFairy2024DataGenerator
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.Model
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.client.TexturedModel
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Supplier

fun Model(creator: (TextureMap) -> JsonElement): Model = object : Model(Optional.empty(), Optional.empty()) {
    override fun upload(id: Identifier, textures: TextureMap, modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>): Identifier {
        modelCollector.accept(id) { creator(textures) }
        return id
    }
}

fun Model(parent: Identifier, vararg textureKeys: TextureKey) = Model(Optional.of(parent), Optional.empty(), *textureKeys)

fun Model(parent: Identifier, variant: String, vararg textureKeys: TextureKey) = Model(Optional.of(parent), Optional.of(variant), *textureKeys)


fun TextureMap(vararg entries: Pair<TextureKey, Identifier>, initializer: TextureMap.() -> Unit = {}): TextureMap {
    val textureMap = TextureMap()
    entries.forEach {
        textureMap.put(it.first, it.second)
    }
    initializer(textureMap)
    return textureMap
}

val TextureKey.string get() = this.toString()


fun Model.with(vararg textureEntries: Pair<TextureKey, Identifier>): TexturedModel = TexturedModel.makeFactory({ TextureMap(*textureEntries) }, this).get(Blocks.AIR)


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
