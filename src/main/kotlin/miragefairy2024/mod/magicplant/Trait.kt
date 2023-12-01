package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import java.util.SortedMap

// TraitRegistry

val traitRegistryKey: RegistryKey<Registry<Trait>> = RegistryKey.ofRegistry(Identifier(MirageFairy2024.modId, "trait"))
val traitRegistry: Registry<Trait> = FabricRegistryBuilder.createSimple(traitRegistryKey).attribute(RegistryAttribute.SYNCED).buildAndRegister()


// Trait

class Trait


// TraitStack

class TraitStack(val trait: Trait, val level: Int) {
    init {
        require(level >= 1)
    }
}


// TraitStacks

class TraitStacks private constructor(val traitStackMap: SortedMap<Trait, Int>) {
    init {
        traitStackMap.forEach { (_, level) ->
            require(level >= 1)
        }
    }
}
