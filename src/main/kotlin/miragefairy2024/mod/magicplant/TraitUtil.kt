package miragefairy2024.mod.magicplant

import mirrg.kotlin.hydrogen.unit
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

fun Trait.register(identifier: Identifier) = unit { Registry.register(traitRegistry, identifier, this) }
