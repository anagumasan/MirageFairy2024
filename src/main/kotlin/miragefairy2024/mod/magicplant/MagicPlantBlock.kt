package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.EMPTY_ITEM_STACK
import miragefairy2024.util.createItemStack
import miragefairy2024.util.darkGray
import miragefairy2024.util.darkRed
import miragefairy2024.util.formatted
import miragefairy2024.util.green
import miragefairy2024.util.invoke
import miragefairy2024.util.join
import miragefairy2024.util.randomInt
import miragefairy2024.util.text
import miragefairy2024.util.yellow
import mirrg.kotlin.hydrogen.max
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Fertilizable
import net.minecraft.block.PlantBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION")
abstract class MagicPlantBlock(settings: Settings) : PlantBlock(settings), BlockEntityProvider, Fertilizable {

    // Trait

    protected fun calculateTraitEffects(world: World, blockPos: BlockPos, traitStacks: TraitStacks): MutableTraitEffects {
        val allTraitEffects = MutableTraitEffects()
        traitStacks.traitStackMap.forEach { (trait, level) ->
            val traitEffects = trait.getTraitEffects(world, blockPos, level)
            if (traitEffects != null) allTraitEffects += traitEffects
        }
        return allTraitEffects
    }


    // Drop

    protected fun createSeed(traitStacks: TraitStacks): ItemStack {
        val itemStack = this.asItem().createItemStack()
        setTraitStacks(itemStack, traitStacks)
        return itemStack
    }

    protected fun calculateCrossedSeed(world: World, blockPos: BlockPos, traitStacks: TraitStacks): ItemStack {

        val targetTraitStacksList = mutableListOf<TraitStacks>()
        fun check(targetBlockPos: BlockPos) {
            val targetBlockState = world.getBlockState(targetBlockPos)
            val targetBlock = targetBlockState.block as? MagicPlantBlock ?: return
            if (targetBlock != this) return
            if (!targetBlock.canCross(world, blockPos, targetBlockState)) return
            val targetTraitStacks = world.getTraitStacks(targetBlockPos) ?: return
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

    abstract fun canCross(world: World, blockPos: BlockPos, blockState: BlockState): Boolean

    final override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
        val traitStacks = world.getTraitStacks(pos) ?: return EMPTY_ITEM_STACK
        return createSeed(traitStacks)
    }

    // 経験値のドロップを onStacksDropped で行うと BlockEntity が得られないためこちらで実装する
    final override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) run {
            if (world !is ServerWorld) return@run
            val traitStacks = world.getTraitStacks(pos) ?: return@run
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

abstract class MagicPlantBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state) {

    private var traitStacks: TraitStacks? = null

    fun getTraitStacks() = traitStacks

    fun setTraitStacks(traitStacks: TraitStacks) {
        this.traitStacks = traitStacks
        markDirty()
    }

    override fun setWorld(world: World) {
        super.setWorld(world)
        if (traitStacks == null) {
            val block = world.getBlockState(pos).block
            val traitStackList = mutableListOf<TraitStack>()
            worldGenTraitGenerations.forEach {
                traitStackList += it.spawn(world, pos, block)
            }
            setTraitStacks(TraitStacks.of(traitStackList))
        }
    }

    public override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        traitStacks?.let { nbt.put("TraitStacks", it.toNbt()) }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        traitStacks = TraitStacks.readFromNbt(nbt)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = super.toInitialChunkDataNbt()
        traitStacks?.let { nbt.put("TraitStacks", it.toNbt()) }
        return nbt
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? = BlockEntityUpdateS2CPacket.create(this)

}

fun BlockView.getTraitStacks(blockPos: BlockPos): TraitStacks? {
    val blockEntity = this.getBlockEntity(blockPos) as? MagicPlantBlockEntity ?: return null
    return blockEntity.getTraitStacks()
}

class MagicPlantSeedItem(block: Block, settings: Settings) : AliasedBlockItem(block, settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        if (world == null) return
        val player = MirageFairy2024.clientProxy?.getClientPlayer() ?: return

        // 特性を得る、無い場合はクリエイティブ専用
        val traitStacks = stack.getTraitStacks() ?: run {
            tooltip += text { CREATIVE_ONLY_TRANSLATION().yellow }
            return
        }

        // プレイヤーのメインハンドの種子の特性を得る
        val otherTraitStacks = if (player.mainHandStack.item == this) player.mainHandStack.getTraitStacks() else null

        // ヘッダー行
        run {
            val countText = when {
                otherTraitStacks == null -> text { "${traitStacks.traitStackList.size}"() }
                traitStacks.traitStackList.size > otherTraitStacks.traitStackList.size -> text { "${traitStacks.traitStackList.size}"().green }
                traitStacks.traitStackList.size == otherTraitStacks.traitStackList.size -> text { "${traitStacks.traitStackList.size}"().darkGray }
                else -> text { "${traitStacks.traitStackList.size}"().darkRed }
            }
            val bitCountText = when {
                otherTraitStacks == null -> text { "${traitStacks.bitCount}"() }
                traitStacks.bitCount > otherTraitStacks.bitCount -> text { "${traitStacks.bitCount}"().green }
                traitStacks.bitCount == otherTraitStacks.bitCount -> text { "${traitStacks.bitCount}"().darkGray }
                else -> text { "${traitStacks.bitCount}"().darkRed }
            }
            tooltip += text { TRAIT_TRANSLATION() + ": x"() + countText + " ("() + bitCountText + "b)"() }
        }

        // 特性行
        traitStacks.traitStackMap.entries
            .sortedBy { it.key }
            .forEach { (trait, level) ->
                val levelText = when {
                    otherTraitStacks == null -> text { level.toString(2)() }

                    else -> {
                        val otherLevel = otherTraitStacks.traitStackMap[trait] ?: 0
                        val bits = (level max otherLevel).toString(2).length
                        (bits - 1 downTo 0).map { bit ->
                            val mask = 1 shl bit
                            val possession = if (level and mask != 0) 1 else 0
                            val otherPossession = if (otherLevel and mask != 0) 1 else 0
                            when {
                                possession > otherPossession -> text { "$possession"().green }
                                possession == otherPossession -> text { "$possession"().darkGray }
                                else -> text { "$possession"().darkRed }
                            }
                        }.join()
                    }
                }

                val traitEffects = trait.getTraitEffects(world, player.blockPos, level)
                tooltip += if (traitEffects != null) {
                    val description = text {
                        traitEffects.effects
                            .map { it.getDescription() }
                            .reduce { a, b -> a + ","() + b }
                    }
                    text { ("  "() + trait.getName() + " "() + levelText + " ("() + description + ")"()).formatted(trait.color) }
                } else {
                    text { ("  "() + trait.getName() + " "() + levelText + " ("() + INVALID_TRANSLATION() + ")"()).darkGray }
                }
            }

    }

    override fun place(context: ItemPlacementContext): ActionResult {
        if (context.stack.getTraitStacks() != null) {
            return super.place(context)
        } else {
            val player = context.player ?: return ActionResult.FAIL
            if (!player.isCreative) return ActionResult.FAIL
            return super.place(context)
        }
    }
}

fun ItemStack.getTraitStacks(): TraitStacks? {
    val nbt = this.nbt ?: return null
    return TraitStacks.readFromNbt(nbt)
}

fun setTraitStacks(itemStack: ItemStack, traitStacks: TraitStacks) {
    itemStack.getOrCreateNbt().put("TraitStacks", traitStacks.toNbt())
}
