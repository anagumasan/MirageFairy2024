package miragefairy2024.mod.magicplant

import miragefairy2024.util.EMPTY_ITEM_STACK
import miragefairy2024.util.createItemStack
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.block.SideShapeType
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

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
            itemStack.getOrCreateNbt().put("TraitStacks", traitStacks.toNbt())
            return itemStack
        }

        private fun getTraitStacks(world: BlockView, blockPos: BlockPos): TraitStacks? {
            val blockEntity = world.getBlockEntity(blockPos) as? MirageFlowerBlockEntity ?: return null
            return blockEntity.getTraitStacks()
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


    // Drop

    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
        val traitStacks = getTraitStacks(world, pos) ?: return EMPTY_ITEM_STACK
        return createSeed(traitStacks)
    }

}

class MirageFlowerBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(MagicPlantCard.MIRAGE_FLOWER.blockEntityType, pos, state) {

    private var traitStacks: TraitStacks? = null

    fun getTraitStacks() = traitStacks

    fun setTraitStacks(traitStacks: TraitStacks) {
        this.traitStacks = traitStacks
        markDirty()
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
