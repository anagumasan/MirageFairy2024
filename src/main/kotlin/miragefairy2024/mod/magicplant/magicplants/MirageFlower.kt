package miragefairy2024.mod.magicplant.magicplants

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import miragefairy2024.MirageFairy2024
import miragefairy2024.MirageFairy2024DataGenerator
import miragefairy2024.mod.MaterialCard
import miragefairy2024.mod.Poem
import miragefairy2024.mod.magicplant.MagicPlantBlock
import miragefairy2024.mod.magicplant.MagicPlantBlockEntity
import miragefairy2024.mod.magicplant.MagicPlantCard
import miragefairy2024.mod.magicplant.MutableTraitEffects
import miragefairy2024.mod.magicplant.TraitCard
import miragefairy2024.mod.magicplant.TraitEffectKeyCard
import miragefairy2024.mod.magicplant.TraitStacks
import miragefairy2024.mod.magicplant.WorldGenTraitRecipe
import miragefairy2024.mod.magicplant.WorldGenTraitRecipeInitScope
import miragefairy2024.mod.magicplant.initMagicPlant
import miragefairy2024.util.HumidityCategory
import miragefairy2024.util.TemperatureCategory
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
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
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
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.CountMultilayerPlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier
import net.minecraft.world.gen.stateprovider.BlockStateProvider

object MirageFlowerCard : MagicPlantCard<MirageFlowerBlock, MirageFlowerBlockEntity>(
    "mirage_flower", "Mirage Flower", "妖花ミラージュ",
    "mirage_bulb", "Mirage Bulb", "ミラージュの球根",
    listOf(
        Poem("Evolution to escape extermination", "可憐にして人畜無害たる魔物。"),
        Poem("classification", "Order Miragales, family Miragaceae", "妖花目ミラージュ科"),
    ),
    { MirageFlowerBlock(createCommonSettings().breakInstantly().mapColor(MapColor.DIAMOND_BLUE).sounds(BlockSoundGroup.GLASS)) },
    ::MirageFlowerBlockEntity,
)

val fairyRingFeature = FairyRingFeature(FairyRingFeatureConfig.CODEC)
val mirageClusterConfiguredFeatureKey: RegistryKey<ConfiguredFeature<*, *>> = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier(MirageFairy2024.modId, "mirage_cluster"))
val largeMirageClusterConfiguredFeatureKey: RegistryKey<ConfiguredFeature<*, *>> = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier(MirageFairy2024.modId, "large_mirage_cluster"))
val mirageClusterPlacedFeatureKey: RegistryKey<PlacedFeature> = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier(MirageFairy2024.modId, "mirage_cluster"))
val netherMirageClusterPlacedFeatureKey: RegistryKey<PlacedFeature> = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier(MirageFairy2024.modId, "nether_mirage_cluster"))
val largeMirageClusterPlacedFeatureKey: RegistryKey<PlacedFeature> = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier(MirageFairy2024.modId, "large_mirage_cluster"))

fun initMirageFlower() {
    val card = MirageFlowerCard
    card.initMagicPlant()

    // 見た目
    card.block.registerVariantsBlockStateGeneration { normal("block/" concat card.blockIdentifier) with MirageFlowerBlock.AGE }
    MirageFlowerBlock.AGE.values.forEach { age ->
        val texturedModel = Models.CROSS.with(TextureKey.CROSS to ("block/" concat card.blockIdentifier concat "_age$age"))
        texturedModel.registerModelGeneration("block/" concat card.blockIdentifier concat "_age$age")
    }

    // 地形生成
    Registry.register(Registries.FEATURE, Identifier(MirageFairy2024.modId, "fairy_ring"), fairyRingFeature)
    run {
        // ミラージュの小さな塊
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.CONFIGURED_FEATURE) { context ->
                val blockStateProvider = BlockStateProvider.of(card.block.withAge(MirageFlowerBlock.MAX_AGE))
                val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(6, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
                context.register(mirageClusterConfiguredFeatureKey, configuredFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGenerationRegistries += RegistryKeys.CONFIGURED_FEATURE

        // ミラージュの大きな塊
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.CONFIGURED_FEATURE) { context ->
                val blockStateProvider = BlockStateProvider.of(card.block.withAge(MirageFlowerBlock.MAX_AGE))
                val configuredFeature = fairyRingFeature with FairyRingFeatureConfig(100, 6F, 8F, 3, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
                context.register(largeMirageClusterConfiguredFeatureKey, configuredFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGenerationRegistries += RegistryKeys.CONFIGURED_FEATURE

        // 地上とエンド
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.PLACED_FEATURE) { context ->
                val placementModifiers = listOf(
                    RarityFilterPlacementModifier.of(16),
                    SquarePlacementModifier.of(),
                    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                    BiomePlacementModifier.of(),
                )
                val placedFeature = PlacedFeature(context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(mirageClusterConfiguredFeatureKey), placementModifiers)
                context.register(mirageClusterPlacedFeatureKey, placedFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGenerationRegistries += RegistryKeys.PLACED_FEATURE
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION, mirageClusterPlacedFeatureKey)
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd().and(BiomeSelectors.excludeByKey(BiomeKeys.THE_END)), GenerationStep.Feature.VEGETAL_DECORATION, mirageClusterPlacedFeatureKey)

        // ネザー
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.PLACED_FEATURE) { context ->
                val placementModifiers = listOf(
                    RarityFilterPlacementModifier.of(64),
                    CountMultilayerPlacementModifier.of(1),
                    BiomePlacementModifier.of(),
                )
                val placedFeature = PlacedFeature(context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(mirageClusterConfiguredFeatureKey), placementModifiers)
                context.register(netherMirageClusterPlacedFeatureKey, placedFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGenerationRegistries += RegistryKeys.PLACED_FEATURE
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.VEGETAL_DECORATION, netherMirageClusterPlacedFeatureKey)

        // 地上の妖精の輪
        MirageFairy2024DataGenerator.onBuildRegistry += {
            it.addRegistry(RegistryKeys.PLACED_FEATURE) { context ->
                val placementModifiers = listOf(
                    RarityFilterPlacementModifier.of(600),
                    SquarePlacementModifier.of(),
                    PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                    BiomePlacementModifier.of(),
                )
                val placedFeature = PlacedFeature(context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(largeMirageClusterConfiguredFeatureKey), placementModifiers)
                context.register(largeMirageClusterPlacedFeatureKey, placedFeature)
            }
        }
        MirageFairy2024DataGenerator.dynamicGenerationRegistries += RegistryKeys.PLACED_FEATURE
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION, largeMirageClusterPlacedFeatureKey)
    }

    // 特性
    WorldGenTraitRecipeInitScope(card.block).run {

        // 標準特性
        registerWorldGenTraitRecipe("A.RS", TraitCard.ETHER_RESPIRATION) // エーテル呼吸
        registerWorldGenTraitRecipe("A.RS", TraitCard.AIR_ADAPTATION) // 空気適応
        registerWorldGenTraitRecipe("..NR", TraitCard.SEEDS_PRODUCTION) // 種子生成
        registerWorldGenTraitRecipe("N.NR", TraitCard.FRUITS_PRODUCTION) // 果実生成
        registerWorldGenTraitRecipe("..NR", TraitCard.LEAVES_PRODUCTION) // 葉面生成
        registerWorldGenTraitRecipe("..NR", TraitCard.FAIRY_BLESSING) // 妖精の祝福

        // R特性
        registerWorldGenTraitRecipe("..RS", TraitCard.PHOTOSYNTHESIS) // 光合成
        registerWorldGenTraitRecipe("..RS", TraitCard.OSMOTIC_ABSORPTION) // 浸透吸収
        registerWorldGenTraitRecipe("RS..", TraitCard.CRYSTAL_ABSORPTION) // 鉱物吸収
        registerWorldGenTraitRecipe("..RS", TraitCard.EXPERIENCE_PRODUCTION) // 経験値生成

        // SR特性
        registerWorldGenTraitRecipe(".S..", TraitCard.PHAEOSYNTHESIS) // 闇合成

        // 環境依存特性
        registerWorldGenTraitRecipe(".A..", TraitCard.ETHER_RESPIRATION, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.IN_THE_END)) // エーテル呼吸
        registerWorldGenTraitRecipe(".A..", TraitCard.AIR_ADAPTATION, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.IN_THE_END)) // 空気適応
        registerWorldGenTraitRecipe(".NRS", TraitCard.COLD_ADAPTATION, WorldGenTraitRecipe.Condition.Temperature(TemperatureCategory.LOW)) // 寒冷適応
        registerWorldGenTraitRecipe(".NRS", TraitCard.WARM_ADAPTATION, WorldGenTraitRecipe.Condition.Temperature(TemperatureCategory.MEDIUM)) // 温暖適応
        registerWorldGenTraitRecipe(".NRS", TraitCard.HOT_ADAPTATION, WorldGenTraitRecipe.Condition.Temperature(TemperatureCategory.HIGH)) // 熱帯適応
        registerWorldGenTraitRecipe(".NRS", TraitCard.ARID_ADAPTATION, WorldGenTraitRecipe.Condition.Humidity(HumidityCategory.LOW)) // 乾燥適応
        registerWorldGenTraitRecipe(".NRS", TraitCard.MESIC_ADAPTATION, WorldGenTraitRecipe.Condition.Humidity(HumidityCategory.MEDIUM)) // 中湿適応
        registerWorldGenTraitRecipe(".NRS", TraitCard.HUMID_ADAPTATION, WorldGenTraitRecipe.Condition.Humidity(HumidityCategory.HIGH)) // 湿潤適応

        // バイオーム限定特性
        registerWorldGenTraitRecipe(".NRS", TraitCard.FOUR_LEAFED, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.FLORAL)) // 四つ葉
        registerWorldGenTraitRecipe(".NRS", TraitCard.NODED_STEM, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.BEACH)) // 節状の茎
        registerWorldGenTraitRecipe(".NRS", TraitCard.FRUIT_OF_KNOWLEDGE, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.JUNGLE)) // 知識の果実
        registerWorldGenTraitRecipe(".NRS", TraitCard.GOLDEN_APPLE, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.FOREST)) // 金のリンゴ
        registerWorldGenTraitRecipe(".NRS", TraitCard.SPINY_LEAVES, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.MESA)) // 棘状の葉
        registerWorldGenTraitRecipe(".NRS", TraitCard.DESERT_GEM, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.DESERT)) // 砂漠の宝石
        registerWorldGenTraitRecipe(".NRS", TraitCard.HEATING_MECHANISM, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.SNOWY)) // 発熱機構
        registerWorldGenTraitRecipe(".NRS", TraitCard.WATERLOGGING_TOLERANCE, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.RIVER)) // 浸水耐性
        registerWorldGenTraitRecipe(".NRS", TraitCard.ADVERSITY_FLOWER, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.MOUNTAIN)) // 高嶺の花
        registerWorldGenTraitRecipe(".NRS", TraitCard.FLESHY_LEAVES, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.SAVANNA)) // 肉厚の葉
        registerWorldGenTraitRecipe(".NRS", TraitCard.NATURAL_ABSCISSION, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.TAIGA)) // 自然落果
        registerWorldGenTraitRecipe(".NRS", TraitCard.CARNIVOROUS_PLANT, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.SWAMP)) // 食虫植物
        registerWorldGenTraitRecipe(".NRS", TraitCard.ETHER_PREDATION, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.IN_THE_END)) // エーテル捕食
        registerWorldGenTraitRecipe(".NRS", TraitCard.PAVEMENT_FLOWERS, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.IN_NETHER)) // アスファルトに咲く花
        registerWorldGenTraitRecipe(".NRS", TraitCard.PROSPERITY_OF_SPECIES, WorldGenTraitRecipe.Condition.InBiome(ConventionalBiomeTags.PLAINS)) // 種の繁栄

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

class FairyRingFeatureConfig(val tries: Int, val minRadius: Float, val maxRadius: Float, val ySpread: Int, val feature: RegistryEntry<PlacedFeature>) : FeatureConfig {
    companion object {
        val CODEC: Codec<FairyRingFeatureConfig> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("tries").forGetter(FairyRingFeatureConfig::tries),
                Codec.FLOAT.fieldOf("min_radius").forGetter(FairyRingFeatureConfig::minRadius),
                Codec.FLOAT.fieldOf("max_radius").forGetter(FairyRingFeatureConfig::maxRadius),
                Codec.INT.fieldOf("y_spread").forGetter(FairyRingFeatureConfig::ySpread),
                PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(FairyRingFeatureConfig::feature),
            ).apply(instance, ::FairyRingFeatureConfig)
        }
    }

    init {
        require(tries >= 0)
        require(minRadius >= 0F)
        require(maxRadius >= 0F)
        require(maxRadius >= minRadius)
        require(ySpread >= 0)
    }
}

class FairyRingFeature(codec: Codec<FairyRingFeatureConfig>) : Feature<FairyRingFeatureConfig>(codec) {
    override fun generate(context: FeatureContext<FairyRingFeatureConfig>): Boolean {
        val config = context.config
        val random = context.random
        val originBlockPos = context.origin
        val world = context.world

        var count = 0
        val minRadius = config.minRadius
        val radiusRange = config.maxRadius - minRadius
        val y1 = config.ySpread + 1
        val mutableBlockPos = BlockPos.Mutable()
        for (l in 0 until config.tries) {
            val r = random.nextFloat() * radiusRange + minRadius
            val theta = random.nextFloat() * MathHelper.TAU
            val x = MathHelper.floor(MathHelper.cos(theta) * r)
            val y = random.nextInt(y1) - random.nextInt(y1)
            val z = MathHelper.floor(MathHelper.sin(theta) * r)

            mutableBlockPos.set(originBlockPos, x, y, z)
            if (config.feature.value().generateUnregistered(world, context.generator, random, mutableBlockPos)) {
                count++
            }
        }

        return count > 0
    }
}
