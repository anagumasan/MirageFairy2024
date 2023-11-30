package miragefairy2024.util

import net.minecraft.block.Blocks
import net.minecraft.block.FarmlandBlock
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

fun BlockView.getMoisture(blockPos: BlockPos): Double {
    val blockState = this.getBlockState(blockPos)
    if (blockState.isOf(Blocks.FARMLAND)) return 0.5 + 0.5 * (blockState.get(FarmlandBlock.MOISTURE) / 7.0)
    if (blockState.isIn(BlockTags.DIRT)) return 0.5
    return 0.0
}

fun BlockView.getCrystalErg(blockPos: BlockPos): Double {
    // TODO 妖精の継承を使って判定
    return when (getBlockState(blockPos).block) {

        Blocks.DIAMOND_BLOCK -> 1.0

        Blocks.EMERALD_BLOCK -> 0.8
        Blocks.AMETHYST_BLOCK -> 0.8

        Blocks.GOLD_BLOCK -> 0.6
        Blocks.QUARTZ_BLOCK -> 0.6

        Blocks.LAPIS_BLOCK -> 0.4
        Blocks.REDSTONE_BLOCK -> 0.4
        Blocks.IRON_BLOCK -> 0.4

        Blocks.COAL_BLOCK -> 0.2
        Blocks.COPPER_BLOCK -> 0.2

        else -> 0.0
    }
}
