package miragefairy2024.util

import com.google.gson.JsonElement
import miragefairy2024.MirageFairy2024DataGenerator
import mirrg.kotlin.gson.hydrogen.jsonElement
import mirrg.kotlin.gson.hydrogen.jsonObject
import mirrg.kotlin.gson.hydrogen.jsonObjectNotNull
import mirrg.kotlin.hydrogen.join
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.BlockStateSupplier
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


enum class BlockStateVariantRotation(val degrees: Int) {
    R0(0),
    R90(90),
    R180(180),
    R270(270),
}

class BlockStateVariant(
    val model: Identifier,
    val x: BlockStateVariantRotation? = null,
    val y: BlockStateVariantRotation? = null,
    val uvlock: Boolean? = null,
    val weight: Int? = null,
) {
    fun toJson(): JsonElement = jsonObjectNotNull(
        "model" to model.string.jsonElement,
        x?.let { "x" to x.degrees.jsonElement },
        y?.let { "y" to y.degrees.jsonElement },
        uvlock?.let { "uvlock" to uvlock.jsonElement },
        weight?.let { "weight" to weight.jsonElement },
    )
}

fun Block.registerVariantsBlockStateGeneration(entriesGetter: () -> List<Pair<List<Pair<String, String>>, BlockStateVariant>>) {
    MirageFairy2024DataGenerator.blockStateModelGenerations += {
        it.blockStateCollector.accept(object : BlockStateSupplier {
            override fun get() = jsonObject(
                "variants" to jsonObject(
                    *entriesGetter()
                        .map { (propertiesMap, modelId) ->
                            val propertiesString = propertiesMap
                                .sortedBy { (property, _) -> property }
                                .map { (property, value) -> "$property=$value" }
                                .join(",")
                            propertiesString to modelId
                        }
                        .sortedBy { (propertiesString, _) -> propertiesString }
                        .map { (propertiesString, value) -> propertiesString to value.toJson() }
                        .toTypedArray()
                )
            )

            override fun getBlock() = this@registerVariantsBlockStateGeneration
        })
    }
}

fun Block.registerSingletonBlockStateGeneration() {
    MirageFairy2024DataGenerator.blockStateModelGenerations += {
        it.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(this, "block/" concat this.getIdentifier()))
    }
}
