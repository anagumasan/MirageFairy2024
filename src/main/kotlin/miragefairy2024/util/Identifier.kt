package miragefairy2024.util

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

val Identifier.string get() = this.toString()

infix fun String.concat(identifier: Identifier) = Identifier(identifier.namespace, this + identifier.path)
infix fun Identifier.concat(string: String) = Identifier(this.namespace, this.path + string)

val Block.identifier get() = Registries.BLOCK.getId(this)
val Item.identifier get() = Registries.ITEM.getId(this)
