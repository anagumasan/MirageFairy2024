package miragefairy2024.util

import mirrg.kotlin.hydrogen.unit
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

fun Block.register(identifier: Identifier) = unit { Registry.register(Registries.BLOCK, identifier, this) }

/** レジストリに登録する前に呼び出すことはできません。 */
fun Block.getIdentifier() = Registries.BLOCK.getId(this)

fun Identifier.toBlock() = Registries.BLOCK.get(this)


fun BlockEntityType<*>.register(identifier: Identifier) = unit { Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, this) }


fun Item.register(identifier: Identifier) = unit { Registry.register(Registries.ITEM, identifier, this) }

/** レジストリに登録する前に呼び出すことはできません。 */
fun Item.getIdentifier() = Registries.ITEM.getId(this)

fun Identifier.toItem() = Registries.ITEM.get(this)


fun ItemGroup.register(identifier: Identifier) = unit { Registry.register(Registries.ITEM_GROUP, identifier, this) }
