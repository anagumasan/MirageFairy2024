package miragefairy2024.mod.magicplant

import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

val worldGenTraitGenerations = mutableListOf<WorldGenTraitGeneration>()

fun interface WorldGenTraitGeneration {
    fun spawn(world: World, blockPos: BlockPos, block: Block): List<TraitStack>
}
