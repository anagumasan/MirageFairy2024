package miragefairy2024.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

fun Vec3d.toBlockPos(): BlockPos = BlockPos.ofFloored(this)

fun BlockPos.toBox() = Box(this)
