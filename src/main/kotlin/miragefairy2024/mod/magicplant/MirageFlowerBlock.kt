package miragefairy2024.mod.magicplant

import miragefairy2024.mod.MaterialCard
import miragefairy2024.util.EMPTY_ITEM_STACK
import miragefairy2024.util.createItemStack
import miragefairy2024.util.randomInt
import miragefairy2024.util.toBlockPos
import miragefairy2024.util.toBox
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import mirrg.kotlin.hydrogen.or
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.block.SideShapeType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

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

        fun createSeed(traitStacks: TraitStacks): ItemStack {
            val itemStack = MagicPlantCard.MIRAGE_FLOWER.item.createItemStack()
            setTraitStacks(itemStack, traitStacks)
            return itemStack
        }

        private fun getTraitStacks(world: BlockView, blockPos: BlockPos): TraitStacks? {
            val blockEntity = world.getBlockEntity(blockPos) as? MirageFlowerBlockEntity ?: return null
            return blockEntity.getTraitStacks()
        }

        private fun calculateCrossedSeed(world: World, blockPos: BlockPos, block: Block, traitStacks: TraitStacks): ItemStack {

            val targetTraitStacksList = mutableListOf<TraitStacks>()
            fun check(targetBlockPos: BlockPos) {
                val targetBlockState = world.getBlockState(targetBlockPos)
                val targetBlock = targetBlockState.block as? MirageFlowerBlock ?: return
                if (targetBlock != block) return
                if (!targetBlock.isMaxAge(targetBlockState)) return
                val targetTraitStacks = getTraitStacks(world, targetBlockPos) ?: return
                targetTraitStacksList += targetTraitStacks
            }
            check(blockPos.north())
            check(blockPos.south())
            check(blockPos.west())
            check(blockPos.east())

            if (targetTraitStacksList.isEmpty()) return createSeed(traitStacks)
            val targetTraitStacks = targetTraitStacksList[world.random.nextInt(targetTraitStacksList.size)]

            return createSeed(crossTraitStacks(world.random, traitStacks, targetTraitStacks))
        }

        private fun getAdditionalDrops(world: World, blockPos: BlockPos, block: Block, traitStacks: TraitStacks, traitEffects: MutableTraitEffects, player: PlayerEntity?, tool: ItemStack?): List<ItemStack> {
            val drops = mutableListOf<ItemStack>()

            val fortune = if (tool != null) EnchantmentHelper.getLevel(Enchantments.FORTUNE, tool).toDouble() else 0.0
            val luck = player?.getAttributeValue(EntityAttributes.GENERIC_LUCK) ?: 0.0

            val seedGeneration = traitEffects[TraitEffectKeyCard.SEEDS_PRODUCTION.traitEffectKey]
            val fruitGeneration = traitEffects[TraitEffectKeyCard.FRUITS_PRODUCTION.traitEffectKey]
            val leafGeneration = traitEffects[TraitEffectKeyCard.LEAVES_PRODUCTION.traitEffectKey]
            val generationBoost = traitEffects[TraitEffectKeyCard.PRODUCTION_BOOST.traitEffectKey]
            val fortuneFactor = traitEffects[TraitEffectKeyCard.FORTUNE_FACTOR.traitEffectKey]

            val seedCount = world.random.randomInt(seedGeneration * (1.0 + generationBoost) * (1.0 + (fortune + luck) * fortuneFactor))
            repeat(seedCount) {
                drops += calculateCrossedSeed(world, blockPos, block, traitStacks)
            }

            val fruitCount = world.random.randomInt(fruitGeneration * (1.0 + generationBoost) * (1.0 + (fortune + luck) * fortuneFactor))
            if (fruitCount > 0) drops += MaterialCard.MIRAGE_FLOUR.item.createItemStack(fruitCount) // TODO 必要であれば圧縮

            val leafCount = world.random.randomInt(leafGeneration * (1.0 + generationBoost) * (1.0 + (fortune + luck) * fortuneFactor))
            if (leafCount > 0) drops += MaterialCard.MIRAGE_LEAVES.item.createItemStack(leafCount)

            return drops
        }

        private fun calculateTraitEffects(world: World, blockPos: BlockPos, traitStacks: TraitStacks): MutableTraitEffects {
            val allTraitEffects = MutableTraitEffects()
            traitStacks.traitStackMap.forEach { (trait, level) ->
                val traitEffects = trait.getTraitEffects(world, blockPos, level)
                if (traitEffects != null) allTraitEffects += traitEffects
            }
            return allTraitEffects
        }
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

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        run {
            if (world.isClient) return@run
            val blockEntity = world.getBlockEntity(pos) as? MirageFlowerBlockEntity ?: return@run
            val traitStacks = itemStack.getTraitStacks() ?: return@run
            blockEntity.setTraitStacks(traitStacks)
        }
    }


    // Growth

    private fun move(world: ServerWorld, pos: BlockPos, state: BlockState, speed: Double = 1.0, autoPick: Boolean = false) {
        val traitStacks = getTraitStacks(world, pos) ?: return
        val traitEffects = calculateTraitEffects(world, pos, traitStacks)

        val nutrition = traitEffects[TraitEffectKeyCard.NUTRITION.traitEffectKey]
        val environment = traitEffects[TraitEffectKeyCard.ENVIRONMENT.traitEffectKey]
        val growthBoost = traitEffects[TraitEffectKeyCard.GROWTH_BOOST.traitEffectKey]

        val actualGrowthAmount = world.random.randomInt(nutrition * environment * (1 + growthBoost) * speed)
        val oldAge = getAge(state)
        val newAge = oldAge + actualGrowthAmount atMost MAX_AGE
        if (newAge != oldAge) {
            world.setBlockState(pos, withAge(newAge), NOTIFY_LISTENERS)
        }

        run {
            if (!autoPick) return@run // 自動収穫が無効の場合は中止
            if (newAge < MAX_AGE) return@run // 最大成長時でない場合は中止
            if (world.getEntitiesByType(EntityType.ITEM, pos.toBox()) { true }.isNotEmpty()) return@run // アイテムがそこに存在する場合は中止
            if (world.getEntitiesByType(EntityType.EXPERIENCE_ORB, pos.toBox()) { true }.isNotEmpty()) return@run // 経験値がそこに存在する場合は中止
            val naturalAbscission = traitEffects[TraitEffectKeyCard.NATURAL_ABSCISSION.traitEffectKey]
            if (!(world.random.nextDouble() < naturalAbscission)) return@run // 確率で失敗
            pick(world, pos, null, null)
        }

    }

    override fun hasRandomTicks(state: BlockState) = true
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) = move(world, pos, state, autoPick = true)

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState) = !isMaxAge(state)
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true
    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) = move(world, pos, state, speed = 10.0)


    // Drop

    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
        val traitStacks = getTraitStacks(world, pos) ?: return EMPTY_ITEM_STACK
        return createSeed(traitStacks)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (!isMaxAge(state)) return ActionResult.PASS
        if (world.isClient) return ActionResult.SUCCESS
        pick(world as ServerWorld, pos, player, player.mainHandStack)
        return ActionResult.CONSUME
    }

    private fun pick(world: ServerWorld, blockPos: BlockPos, player: PlayerEntity?, tool: ItemStack?) {

        // ドロップアイテムを計算
        val block = world.getBlockState(blockPos).block
        val traitStacks = getTraitStacks(world, blockPos) ?: return
        val traitEffects = calculateTraitEffects(world, blockPos, traitStacks)
        val drops = getAdditionalDrops(world, blockPos, block, traitStacks, traitEffects, player, tool)
        val experience = world.random.randomInt(traitEffects[TraitEffectKeyCard.EXPERIENCE_PRODUCTION.traitEffectKey])

        // アイテムを生成
        drops.forEach { itemStack ->
            dropStack(world, blockPos, itemStack)
        }
        if (experience > 0) dropExperience(world, blockPos, experience)

        // 成長段階を消費
        world.setBlockState(blockPos, withAge(0), NOTIFY_LISTENERS)

        // エフェクト
        world.playSound(null, blockPos, soundGroup.breakSound, SoundCategory.BLOCKS, (soundGroup.volume + 1.0F) / 2.0F * 0.5F, soundGroup.pitch * 0.8F)

    }

    // 本来 LootTable を使ってすべて行う想定だが、他にドロップを自由に制御できる場所がないため苦肉の策でここでプログラムで生成する
    override fun getDroppedStacks(state: BlockState, builder: LootContextParameterSet.Builder): MutableList<ItemStack> {
        val itemStacks = mutableListOf<ItemStack>()
        @Suppress("DEPRECATION")
        itemStacks += super.getDroppedStacks(state, builder)
        run {
            val world = builder.world ?: return@run
            val blockPos = builder.getOptional(LootContextParameters.ORIGIN).or { return@run }.toBlockPos()
            val blockState = builder.getOptional(LootContextParameters.BLOCK_STATE) ?: return@run
            val block = blockState.block
            val blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY) as? MirageFlowerBlockEntity ?: return@run
            val traitStacks = blockEntity.getTraitStacks() ?: return@run
            val traitEffects = calculateTraitEffects(world, blockPos, traitStacks)
            val player = builder.getOptional(LootContextParameters.THIS_ENTITY) as? PlayerEntity
            val tool = builder.getOptional(LootContextParameters.TOOL)

            itemStacks += createSeed(traitStacks)
            if (isMaxAge(state)) itemStacks += getAdditionalDrops(world, blockPos, block, traitStacks, traitEffects, player, tool)
        }
        return itemStacks
    }

    // 経験値のドロップを onStacksDropped で行うと BlockEntity が得られないためこちらで実装する
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) run {
            if (world !is ServerWorld) return@run
            val traitStacks = getTraitStacks(world, pos) ?: return@run
            val traitEffects = calculateTraitEffects(world, pos, traitStacks)
            val experience = world.random.randomInt(traitEffects[TraitEffectKeyCard.EXPERIENCE_PRODUCTION.traitEffectKey])
            if (experience > 0) dropExperience(world, pos, experience)
        }
        @Suppress("DEPRECATION")
        super.onStateReplaced(state, world, pos, newState, moved)
    }


    // Visual

    // TODO パーティクル

}

class MirageFlowerBlockEntity(pos: BlockPos, state: BlockState) : MagicPlantBlockEntity(MagicPlantCard.MIRAGE_FLOWER.blockEntityType, pos, state)
