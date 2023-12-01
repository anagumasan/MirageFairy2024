package miragefairy2024.mod.magicplant

import mirrg.kotlin.hydrogen.unit
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

// Trait

fun Trait.register(identifier: Identifier) = unit { Registry.register(traitRegistry, identifier, this) }

fun Trait.getIdentifier() = traitRegistry.getId(this)!!
fun Identifier.toTrait() = traitRegistry.get(this)


// TraitEffectKey

fun TraitEffectKey.register(identifier: Identifier) = unit { Registry.register(traitEffectKeyRegistry, identifier, this) }

fun TraitEffectKey.getIdentifier() = traitEffectKeyRegistry.getId(this)!!
fun Identifier.toTraitEffectKey() = traitEffectKeyRegistry.get(this)


// TraitStacks

fun ItemStack.getTraitStacks(): TraitStacks? {
    val nbt = this.nbt ?: return null
    return TraitStacks.readFromNbt(nbt)
}
