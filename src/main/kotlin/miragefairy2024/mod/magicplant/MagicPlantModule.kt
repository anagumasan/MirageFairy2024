package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import miragefairy2024.mod.Poem
import miragefairy2024.mod.mirageFairy2024ItemGroup
import miragefairy2024.mod.registerPoem
import miragefairy2024.mod.registerPoemGeneration
import miragefairy2024.util.BlockStateVariant
import miragefairy2024.util.Chance
import miragefairy2024.util.HumidityCategory
import miragefairy2024.util.TemperatureCategory
import miragefairy2024.util.Translation
import miragefairy2024.util.chanceTo
import miragefairy2024.util.concat
import miragefairy2024.util.enJa
import miragefairy2024.util.humidityCategory
import miragefairy2024.util.register
import miragefairy2024.util.registerComposterInput
import miragefairy2024.util.registerCutoutRenderLayer
import miragefairy2024.util.registerGeneratedItemModelGeneration
import miragefairy2024.util.registerItemGroup
import miragefairy2024.util.registerModelGeneration
import miragefairy2024.util.registerVariantsBlockStateGeneration
import miragefairy2024.util.temperatureCategory
import miragefairy2024.util.weightedRandom
import miragefairy2024.util.with
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.block.BlockState
import net.minecraft.block.MapColor
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

class MagicPlantCard<B : MagicPlantBlock, BE : BlockEntity>(
    blockPath: String,
    val blockEnName: String,
    val blockJaName: String,
    itemPath: String,
    val itemEnName: String,
    val itemJaName: String,
    val seedPoemList: List<Poem>,
    blockCreator: () -> B,
    blockEntityCreator: (BlockPos, BlockState) -> BE,
) {
    companion object {
        val MIRAGE_FLOWER = MagicPlantCard(
            "mirage_flower", "Mirage Flower", "ミラージュの花",
            "mirage_bulb", "Mirage Bulb", "ミラージュの球根",
            listOf(
                Poem("Evolution to escape extermination", "可憐にして人畜無害たる魔物。"),
                Poem("classification", "Order Miragales, family Miragaceae", "妖花目ミラージュ科"),
            ),
            { MirageFlowerBlock(createCommonSettings().breakInstantly().mapColor(MapColor.DIAMOND_BLUE).sounds(BlockSoundGroup.GLASS)) },
            ::MirageFlowerBlockEntity,
        )

        private fun createCommonSettings(): FabricBlockSettings = FabricBlockSettings.create().noCollision().ticksRandomly().pistonBehavior(PistonBehavior.DESTROY)
    }

    val blockIdentifier = Identifier(MirageFairy2024.modId, blockPath)
    val itemIdentifier = Identifier(MirageFairy2024.modId, itemPath)
    val block = blockCreator()
    val blockEntityType = BlockEntityType(blockEntityCreator, setOf(block), null)
    val item = MagicPlantSeedItem(block, Item.Settings())
}

fun initMagicPlantModule() {

    fun init(card: MagicPlantCard<*, *>) {
        card.block.register(card.blockIdentifier)
        card.blockEntityType.register(card.blockIdentifier)
        card.item.register(card.itemIdentifier)

        card.item.registerItemGroup(mirageFairy2024ItemGroup)

        card.block.registerCutoutRenderLayer()
        card.item.registerGeneratedItemModelGeneration()

        card.block.enJa(card.blockEnName, card.blockJaName)
        card.item.enJa(card.itemEnName, card.itemJaName)
        card.item.registerPoem(card.seedPoemList)
        card.item.registerPoemGeneration(card.seedPoemList)

        card.item.registerComposterInput(0.3F) // 種はコンポスターに投入可能
    }

    MagicPlantCard.MIRAGE_FLOWER.let { card ->
        init(card)

        card.block.registerVariantsBlockStateGeneration {
            (0..MirageFlowerBlock.MAX_AGE).map { age ->
                listOf("age" to "$age") to BlockStateVariant("block/" concat card.blockIdentifier concat "_age$age")
            }
        }
        (0..MirageFlowerBlock.MAX_AGE).forEach { age ->
            val texturedModel = Models.CROSS.with(TextureKey.CROSS to ("block/" concat card.blockIdentifier concat "_age$age"))
            texturedModel.registerModelGeneration("block/" concat card.blockIdentifier concat "_age$age")
        }

        //card.block.registerTagGenerate(BlockTags.SMALL_FLOWERS) // これをやるとエンダーマンが勝手に引っこ抜いていく

        // TODO worldgen
    }


    TraitEffectKeyCard.entries.forEach { card ->
        card.traitEffectKey.register(card.identifier)
        card.traitEffectKey.enJa(card.enName, card.jaName)
    }

    TRAIT_TRANSLATION.enJa()
    CREATIVE_ONLY_TRANSLATION.enJa()
    INVALID_TRANSLATION.enJa()

    TraitCard.entries.forEach { card ->
        card.trait.register(card.identifier)
        card.trait.enJa(card.enName, card.jaName)
    }

    worldGenTraitGenerations += WorldGenTraitGeneration { world, blockPos, block ->
        val traitStackList = mutableListOf<TraitStack>()

        val rareTraitStacks = mutableListOf<Chance<TraitStack>>()
        val sRareTraitStacks = mutableListOf<Chance<TraitStack>>()

        fun N(binary: String, vararg alternativeCards: TraitCard, condition: () -> Boolean = { true }) {
            if (condition()) traitStackList += TraitStack(alternativeCards[world.random.nextInt(alternativeCards.size)].trait, binary.toInt(2))
        }

        fun R(binary: String, vararg alternativeCards: TraitCard, condition: () -> Boolean = { true }) {
            if (condition()) rareTraitStacks += 0.1 chanceTo TraitStack(alternativeCards[world.random.nextInt(alternativeCards.size)].trait, binary.toInt(2))
        }

        fun S(binary: String, vararg alternativeCards: TraitCard, condition: () -> Boolean = { true }) {
            if (condition()) sRareTraitStacks += 0.01 chanceTo TraitStack(alternativeCards[world.random.nextInt(alternativeCards.size)].trait, binary.toInt(2))
        }

        val biome by lazy { world.getBiome(blockPos) }
        fun biome(biomeTag: TagKey<Biome>) = biome.isIn(biomeTag)


        // Definition
        run {
            // 環境系
            fun registerAdaptation(traitCard: TraitCard, condition: () -> Boolean) {
                N("0100", traitCard, condition = condition)
                R("0010", traitCard, condition = condition)
            }
            registerAdaptation(TraitCard.COLD_ADAPTATION) { biome.temperatureCategory == TemperatureCategory.LOW }
            registerAdaptation(TraitCard.WARM_ADAPTATION) { biome.temperatureCategory == TemperatureCategory.MEDIUM }
            registerAdaptation(TraitCard.HOT_ADAPTATION) { biome.temperatureCategory == TemperatureCategory.HIGH }
            registerAdaptation(TraitCard.ARID_ADAPTATION) { biome.humidityCategory == HumidityCategory.LOW }
            registerAdaptation(TraitCard.MESIC_ADAPTATION) { biome.humidityCategory == HumidityCategory.MEDIUM }
            registerAdaptation(TraitCard.HUMID_ADAPTATION) { biome.humidityCategory == HumidityCategory.HIGH }

            // バイオーム系
            fun registerBiome(traitCard: TraitCard, biomeTag: TagKey<Biome>) {
                N("0100", traitCard) { biome(biomeTag) }
                R("0010", traitCard) { biome(biomeTag) }
                S("0001", traitCard) { biome(biomeTag) }
            }
            registerBiome(TraitCard.FOUR_LEAFED, ConventionalBiomeTags.FLORAL)
            registerBiome(TraitCard.NODED_STEM, ConventionalBiomeTags.BEACH)
            registerBiome(TraitCard.FRUIT_OF_KNOWLEDGE, ConventionalBiomeTags.JUNGLE)
            registerBiome(TraitCard.GOLDEN_APPLE, ConventionalBiomeTags.FOREST)
            registerBiome(TraitCard.SPINY_LEAVES, ConventionalBiomeTags.MESA)
            registerBiome(TraitCard.DESERT_GEM, ConventionalBiomeTags.DESERT)
            registerBiome(TraitCard.HEATING_MECHANISM, ConventionalBiomeTags.SNOWY)
            registerBiome(TraitCard.WATERLOGGING_TOLERANCE, ConventionalBiomeTags.SWAMP)
            registerBiome(TraitCard.ADVERSITY_FLOWER, ConventionalBiomeTags.MOUNTAIN)
            registerBiome(TraitCard.FLESHY_LEAVES, ConventionalBiomeTags.SAVANNA)
            registerBiome(TraitCard.NATURAL_ABSCISSION, ConventionalBiomeTags.TAIGA)
        }
        if (block == MagicPlantCard.MIRAGE_FLOWER.block) {
            // 栄養系
            N("1000", TraitCard.ETHER_RESPIRATION) { !biome(ConventionalBiomeTags.IN_THE_END) }
            N("0100", TraitCard.ETHER_RESPIRATION) { biome(ConventionalBiomeTags.IN_THE_END) }
            R("0010", TraitCard.ETHER_RESPIRATION)
            S("0001", TraitCard.ETHER_RESPIRATION)
            R("0010", TraitCard.PHOTOSYNTHESIS)
            S("0100", TraitCard.PHAEOSYNTHESIS)
            R("0010", TraitCard.OSMOTIC_ABSORPTION)
            R("1000", TraitCard.CRYSTAL_ABSORPTION)
            S("0100", TraitCard.CRYSTAL_ABSORPTION)
            S("0010", TraitCard.CRYSTAL_ABSORPTION)
            S("0001", TraitCard.CRYSTAL_ABSORPTION)

            // 環境系
            N("1000", TraitCard.AIR_ADAPTATION)
            R("0010", TraitCard.AIR_ADAPTATION)
            S("0001", TraitCard.AIR_ADAPTATION)

            // 生産系
            N("0010", TraitCard.SEEDS_PRODUCTION)
            R("0001", TraitCard.SEEDS_PRODUCTION)
            N("1010", TraitCard.FRUITS_PRODUCTION)
            R("0001", TraitCard.FRUITS_PRODUCTION)
            N("0010", TraitCard.LEAVES_PRODUCTION)
            R("0001", TraitCard.LEAVES_PRODUCTION)
            R("0010", TraitCard.EXPERIENCE_PRODUCTION)
            S("0001", TraitCard.EXPERIENCE_PRODUCTION)

            // 妖精の祝福
            N("0010", TraitCard.FAIRY_BLESSING)
            R("0001", TraitCard.FAIRY_BLESSING)
        }


        // Rare
        if (world.random.nextDouble() < 0.01) {
            val traitStack = sRareTraitStacks.weightedRandom(world.random)
            if (traitStack != null) traitStackList += traitStack
        } else if (world.random.nextDouble() < 0.1) {
            val traitStack = rareTraitStacks.weightedRandom(world.random)
            if (traitStack != null) traitStackList += traitStack
        }

        traitStackList
    }

}

val TRAIT_TRANSLATION = Translation({ "item.magicplant.trait" }, "Trait", "特性")
val CREATIVE_ONLY_TRANSLATION = Translation({ "item.magicplant.creativeOnly" }, "Creative Only", "クリエイティブ専用")
val INVALID_TRANSLATION = Translation({ "item.magicplant.invalid" }, "Invalid", "無効")
