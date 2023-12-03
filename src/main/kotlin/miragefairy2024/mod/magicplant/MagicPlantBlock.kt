package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.darkGray
import miragefairy2024.util.darkRed
import miragefairy2024.util.formatted
import miragefairy2024.util.green
import miragefairy2024.util.invoke
import miragefairy2024.util.join
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
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class MagicPlantBlock(settings: Settings) : PlantBlock(settings), BlockEntityProvider, Fertilizable {
    companion object {
        fun addTraitStacks(itemStack: ItemStack, traitStacks: TraitStacks) {
            itemStack.getOrCreateNbt().put("TraitStacks", traitStacks.toNbt())
        }
    }
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
