package miragefairy2024.util

import mirrg.kotlin.hydrogen.unit
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

fun Block.registerBlock(identifier: Identifier) = unit { Registry.register(Registries.BLOCK, identifier, this) }

fun Item.registerItem(identifier: Identifier) = unit { Registry.register(Registries.ITEM, identifier, this) }
