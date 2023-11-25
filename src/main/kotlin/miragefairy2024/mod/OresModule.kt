package miragefairy2024.mod

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.Model
import miragefairy2024.util.concat
import miragefairy2024.util.enJa
import miragefairy2024.util.register
import miragefairy2024.util.registerCutoutRenderLayer
import miragefairy2024.util.registerItemGroup
import miragefairy2024.util.registerModelGeneration
import miragefairy2024.util.registerOreLootTableGeneration
import miragefairy2024.util.registerSingletonBlockStateGeneration
import miragefairy2024.util.registerTagGeneration
import miragefairy2024.util.string
import miragefairy2024.util.with
import mirrg.kotlin.gson.hydrogen.jsonArray
import mirrg.kotlin.gson.hydrogen.jsonElement
import mirrg.kotlin.gson.hydrogen.jsonObject
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.ExperienceDroppingBlock
import net.minecraft.block.MapColor
import net.minecraft.block.enums.Instrument
import net.minecraft.data.client.TextureKey
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.intprovider.UniformIntProvider

enum class OreCard(
    path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
    val dropItem: Item,
    experience: Pair<Int, Int>,
) {
    MIRANAGITE_ORE(
        "miranagite_ore", "Miranagite Ore", "蒼天石鉱石",
        listOf(Poem("What lies beyond a Garden of Eden?", "秩序の石は楽園の先に何を見るのか？")),
        MaterialCard.MIRANAGITE.item, 2 to 5,
    )
    // 楽園が楽園であるための奇跡。
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val block = run {
        val settings = FabricBlockSettings.create()
            .mapColor(MapColor.STONE_GRAY)
            .instrument(Instrument.BASEDRUM)
            .requiresTool()
            .strength(3.0F, 3.0F)
        ExperienceDroppingBlock(settings, UniformIntProvider.create(experience.first, experience.second))
    }
    val item = BlockItem(block, Item.Settings())
    val texturedModel = OreModelCard.model.with(
        TextureKey.BACK to Identifier("minecraft", "block/stone"),
        TextureKey.FRONT to ("block/" concat identifier),
    )
}

object OreModelCard {
    val parentModel = Model {
        jsonObject(
            "parent" to Identifier("minecraft", "block/block").string.jsonElement,
            "textures" to jsonObject(
                TextureKey.PARTICLE.name to TextureKey.BACK.string.jsonElement,
            ),
            "elements" to jsonArray(
                jsonObject(
                    "from" to jsonArray(0.jsonElement, 0.jsonElement, 0.jsonElement),
                    "to" to jsonArray(16.jsonElement, 16.jsonElement, 16.jsonElement),
                    "faces" to jsonObject(
                        "down" to jsonObject("texture" to TextureKey.BACK.string.jsonElement, "cullface" to "down".jsonElement),
                        "up" to jsonObject("texture" to TextureKey.BACK.string.jsonElement, "cullface" to "up".jsonElement),
                        "north" to jsonObject("texture" to TextureKey.BACK.string.jsonElement, "cullface" to "north".jsonElement),
                        "south" to jsonObject("texture" to TextureKey.BACK.string.jsonElement, "cullface" to "south".jsonElement),
                        "west" to jsonObject("texture" to TextureKey.BACK.string.jsonElement, "cullface" to "west".jsonElement),
                        "east" to jsonObject("texture" to TextureKey.BACK.string.jsonElement, "cullface" to "east".jsonElement),
                    ),
                ),
                jsonObject(
                    "from" to jsonArray(0.jsonElement, 0.jsonElement, 0.jsonElement),
                    "to" to jsonArray(16.jsonElement, 16.jsonElement, 16.jsonElement),
                    "faces" to jsonObject(
                        "down" to jsonObject("texture" to TextureKey.FRONT.string.jsonElement, "cullface" to "down".jsonElement),
                        "up" to jsonObject("texture" to TextureKey.FRONT.string.jsonElement, "cullface" to "up".jsonElement),
                        "north" to jsonObject("texture" to TextureKey.FRONT.string.jsonElement, "cullface" to "north".jsonElement),
                        "south" to jsonObject("texture" to TextureKey.FRONT.string.jsonElement, "cullface" to "south".jsonElement),
                        "west" to jsonObject("texture" to TextureKey.FRONT.string.jsonElement, "cullface" to "west".jsonElement),
                        "east" to jsonObject("texture" to TextureKey.FRONT.string.jsonElement, "cullface" to "east".jsonElement),
                    ),
                ),
            ),
        )
    }
    val identifier = Identifier(MirageFairy2024.modId, "block/ore")
    val model = Model(identifier, TextureKey.BACK, TextureKey.FRONT)
}

fun initOresModule() {
    OreModelCard.parentModel.registerModelGeneration(OreModelCard.identifier)
    OreCard.entries.forEach { card ->
        card.block.register(card.identifier)
        card.item.register(card.identifier)

        card.item.registerItemGroup(mirageFairy2024ItemGroup)

        card.block.registerSingletonBlockStateGeneration()
        card.block.registerModelGeneration(card.texturedModel)
        card.block.registerCutoutRenderLayer()

        card.block.enJa(card.enName, card.jaName)
        card.item.registerPoem(card.poemList)
        card.item.registerPoemGeneration(card.poemList)

        card.block.registerOreLootTableGeneration(card.dropItem)

        card.block.registerTagGeneration(BlockTags.PICKAXE_MINEABLE)
        card.block.registerTagGeneration(BlockTags.NEEDS_STONE_TOOL)

        // TODO worldgen
    }
}
