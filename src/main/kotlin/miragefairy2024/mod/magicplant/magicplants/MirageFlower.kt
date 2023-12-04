package miragefairy2024.mod.magicplant.magicplants

import miragefairy2024.MirageFairy2024
import miragefairy2024.MirageFairy2024DataGenerator
import miragefairy2024.mod.MaterialCard
import miragefairy2024.mod.Poem
import miragefairy2024.mod.magicplant.MagicPlantBlock
import miragefairy2024.mod.magicplant.MagicPlantBlockEntity
import miragefairy2024.mod.magicplant.MagicPlantCard
import miragefairy2024.mod.magicplant.MutableTraitEffects
import miragefairy2024.mod.magicplant.TraitEffectKeyCard
import miragefairy2024.mod.magicplant.TraitStacks
import miragefairy2024.util.BlockStateVariant
import miragefairy2024.util.concat
import miragefairy2024.util.createItemStack
import miragefairy2024.util.randomInt
import miragefairy2024.util.registerModelGeneration
import miragefairy2024.util.registerVariantsBlockStateGeneration
import miragefairy2024.util.with
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.MapColor
import net.minecraft.block.ShapeContext
import net.minecraft.block.SideShapeType
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier
import net.minecraft.world.gen.stateprovider.BlockStateProvider

object MirageFlowerCard : MagicPlantCard<MirageFlowerBlock, MirageFlowerBlockEntity>(
    "mirage_flower", "Mirage Flower", "ミラージュの花",
    "mirage_bulb", "Mirage Bulb", "ミラージュの球根",
    listOf(
        Poem("Evolution to escape extermination", "可憐にして人畜無害たる魔物。"),
        Poem("classification", "Order Miragales, family Miragaceae", "妖花目ミラージュ科"),
    ),
    { MirageFlowerBlock(createCommonSettings().breakInstantly().mapColor(MapColor.DIAMOND_BLUE).sounds(BlockSoundGroup.GLASS)) },
    ::MirageFlowerBlockEntity,
)

fun initMirageFlower() {
    val card = MirageFlowerCard
    card.init()

    // 見た目
    card.block.registerVariantsBlockStateGeneration {
        (0..MirageFlowerBlock.MAX_AGE).map { age ->
            listOf("age" to "$age") to BlockStateVariant("block/" concat card.blockIdentifier concat "_age$age")
        }
    }
    (0..MirageFlowerBlock.MAX_AGE).forEach { age ->
        val texturedModel = Models.CROSS.with(TextureKey.CROSS to ("block/" concat card.blockIdentifier concat "_age$age"))
        texturedModel.registerModelGeneration("block/" concat card.blockIdentifier concat "_age$age")
    }

    // 性質
    //card.block.registerTagGenerate(BlockTags.SMALL_FLOWERS) // これをやるとエンダーマンが勝手に引っこ抜いていく

    // 地形生成
    run { // ミラージュの小さな塊
        val identifier = Identifier(MirageFairy2024.modId, "mirage_cluster")
        val configuredKey = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, identifier)
        val placedKey = RegistryKey.of(RegistryKeys.PLACED_FEATURE, identifier)
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.CONFIGURED_FEATURE) { context ->
                val blockStateProvider = BlockStateProvider.of(card.block.withAge(MirageFlowerBlock.MAX_AGE))
                val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(6, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
                context.register(configuredKey, configuredFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGeneratingRegistries += RegistryKeys.CONFIGURED_FEATURE
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.PLACED_FEATURE) { context ->
                val placementModifiers = listOf(
                    RarityFilterPlacementModifier.of(16),
                    SquarePlacementModifier.of(),
                    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                    BiomePlacementModifier.of(),
                )
                val placedFeature = PlacedFeature(context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(configuredKey), placementModifiers)
                context.register(placedKey, placedFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGeneratingRegistries += RegistryKeys.PLACED_FEATURE
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION, placedKey)
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd().and(BiomeSelectors.excludeByKey(BiomeKeys.THE_END)), GenerationStep.Feature.VEGETAL_DECORATION, placedKey)
    }
    run { // ネザー用ミラージュの塊
        val identifier = Identifier(MirageFairy2024.modId, "nether_mirage_cluster")
        val configuredKey = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, identifier)
        val placedKey = RegistryKey.of(RegistryKeys.PLACED_FEATURE, identifier)
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.CONFIGURED_FEATURE) { context ->
                val blockStateProvider = BlockStateProvider.of(card.block.withAge(MirageFlowerBlock.MAX_AGE))
                val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(40, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
                context.register(configuredKey, configuredFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGeneratingRegistries += RegistryKeys.CONFIGURED_FEATURE
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.PLACED_FEATURE) { context ->
                val placementModifiers = listOf(
                    RarityFilterPlacementModifier.of(2),
                    SquarePlacementModifier.of(),
                    PlacedFeatures.BOTTOM_TO_TOP_RANGE,
                    BiomePlacementModifier.of(),
                )
                val placedFeature = PlacedFeature(context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(configuredKey), placementModifiers)
                context.register(placedKey, placedFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGeneratingRegistries += RegistryKeys.PLACED_FEATURE
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.VEGETAL_DECORATION, placedKey)
    }
    run { // ミラージュの大きな塊
        val identifier = Identifier(MirageFairy2024.modId, "large_mirage_cluster")
        val configuredKey = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, identifier)
        val placedKey = RegistryKey.of(RegistryKeys.PLACED_FEATURE, identifier)
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.CONFIGURED_FEATURE) { context ->
                val blockStateProvider = BlockStateProvider.of(card.block.withAge(MirageFlowerBlock.MAX_AGE))
                val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(100, 8, 3, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
                context.register(configuredKey, configuredFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGeneratingRegistries += RegistryKeys.CONFIGURED_FEATURE
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.PLACED_FEATURE) { context ->
                val placementModifiers = listOf(
                    RarityFilterPlacementModifier.of(600),
                    SquarePlacementModifier.of(),
                    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                    BiomePlacementModifier.of(),
                )
                val placedFeature = PlacedFeature(context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(configuredKey), placementModifiers)
                context.register(placedKey, placedFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGeneratingRegistries += RegistryKeys.PLACED_FEATURE
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION, placedKey)
    }

}

@Suppress("OVERRIDE_DEPRECATION")
class MirageFlowerBlock(settings: Settings) : MagicPlantBlock(settings) {
    companion object {
        val AGE: IntProperty = Properties.AGE_3
        const val MAX_AGE = 3
        private val AGE_TO_SHAPE: Array<VoxelShape> = arrayOf(
            createCuboidShape(5.0, 0.0, 5.0, 11.0, 5.0, 11.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0),
        )
    }


    // Property

    init {
        defaultState = defaultState.with(AGE, 0)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    fun getAge(state: BlockState) = state[AGE]!!
    fun isMaxAge(state: BlockState) = getAge(state) >= MAX_AGE
    fun withAge(age: Int): BlockState = defaultState.with(AGE, age atLeast 0 atMost MAX_AGE)


    // Shape

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = AGE_TO_SHAPE[getAge(state)]
    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos) = world.getBlockState(pos).isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER) || floor.isOf(Blocks.FARMLAND)


    // Block Entity

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = MirageFlowerBlockEntity(pos, state)


    // Magic Plant

    override fun canCross(world: World, blockPos: BlockPos, blockState: BlockState) = isMaxAge(blockState)
    override fun canGrow(blockState: BlockState) = !isMaxAge(blockState)
    override fun getBlockStateAfterGrowth(blockState: BlockState, amount: Int) = withAge(getAge(blockState) + amount atMost MAX_AGE)
    override fun canPick(blockState: BlockState) = isMaxAge(blockState)
    override fun getBlockStateAfterPicking(blockState: BlockState) = withAge(0)

    override fun getAdditionalDrops(world: World, blockPos: BlockPos, block: Block, blockState: BlockState, traitStacks: TraitStacks, traitEffects: MutableTraitEffects, player: PlayerEntity?, tool: ItemStack?): List<ItemStack> {
        val drops = mutableListOf<ItemStack>()

        val fortune = if (tool != null) EnchantmentHelper.getLevel(Enchantments.FORTUNE, tool).toDouble() else 0.0
        val luck = player?.getAttributeValue(EntityAttributes.GENERIC_LUCK) ?: 0.0

        val seedGeneration = traitEffects[TraitEffectKeyCard.SEEDS_PRODUCTION.traitEffectKey]
        val fruitGeneration = traitEffects[TraitEffectKeyCard.FRUITS_PRODUCTION.traitEffectKey]
        val leafGeneration = traitEffects[TraitEffectKeyCard.LEAVES_PRODUCTION.traitEffectKey]
        val generationBoost = traitEffects[TraitEffectKeyCard.PRODUCTION_BOOST.traitEffectKey]
        val fortuneFactor = traitEffects[TraitEffectKeyCard.FORTUNE_FACTOR.traitEffectKey]

        if (isMaxAge(blockState)) {
            val seedCount = world.random.randomInt(seedGeneration * (1.0 + generationBoost) * (1.0 + (fortune + luck) * fortuneFactor))
            repeat(seedCount) {
                drops += calculateCrossedSeed(world, blockPos, traitStacks)
            }
        }

        if (isMaxAge(blockState)) {
            val fruitCount = world.random.randomInt(fruitGeneration * (1.0 + generationBoost) * (1.0 + (fortune + luck) * fortuneFactor))
            if (fruitCount > 0) drops += MaterialCard.MIRAGE_FLOUR.item.createItemStack(fruitCount) // TODO 必要であれば圧縮
        }

        if (isMaxAge(blockState)) {
            val leafCount = world.random.randomInt(leafGeneration * (1.0 + generationBoost) * (1.0 + (fortune + luck) * fortuneFactor))
            if (leafCount > 0) drops += MaterialCard.MIRAGE_LEAVES.item.createItemStack(leafCount)
        }

        return drops
    }

}

class MirageFlowerBlockEntity(pos: BlockPos, state: BlockState) : MagicPlantBlockEntity(MirageFlowerCard.blockEntityType, pos, state)
