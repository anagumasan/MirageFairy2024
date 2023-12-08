package miragefairy2024.util

import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape

fun Vec3d.toBlockPos(): BlockPos = BlockPos.ofFloored(this)

fun BlockPos.toBox() = Box(this)

fun createCuboidShape(radius: Double, height: Double): VoxelShape = Block.createCuboidShape(8 - radius, 0.0, 8 - radius, 8 + radius, height, 8 + radius)
