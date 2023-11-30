package miragefairy2024.util

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

val Identifier.string get() = this.toString()

fun String.toIdentifier() = Identifier(this)

infix fun String.concat(identifier: Identifier) = Identifier(identifier.namespace, this + identifier.path)
infix fun Identifier.concat(string: String) = Identifier(this.namespace, this.path + string)

/** レジストリに登録する前に呼び出すことはできません。 */
fun Block.getIdentifier() = Registries.BLOCK.getId(this)

/** レジストリに登録する前に呼び出すことはできません。 */
fun Item.getIdentifier() = Registries.ITEM.getId(this)
