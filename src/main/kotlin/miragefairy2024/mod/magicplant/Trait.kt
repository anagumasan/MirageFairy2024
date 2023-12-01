package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.string
import miragefairy2024.util.toIdentifier
import mirrg.kotlin.hydrogen.cmp
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import java.util.SortedMap

// TraitRegistry

val traitRegistryKey: RegistryKey<Registry<Trait>> = RegistryKey.ofRegistry(Identifier(MirageFairy2024.modId, "trait"))
val traitRegistry: Registry<Trait> = FabricRegistryBuilder.createSimple(traitRegistryKey).attribute(RegistryAttribute.SYNCED).buildAndRegister()


// TraitEffectKeyRegistry

val traitEffectKeyRegistryKey: RegistryKey<Registry<TraitEffectKey>> = RegistryKey.ofRegistry(Identifier(MirageFairy2024.modId, "trait_effect_key"))
val traitEffectKeyRegistry: Registry<TraitEffectKey> = FabricRegistryBuilder.createSimple(traitEffectKeyRegistryKey).attribute(RegistryAttribute.SYNCED).buildAndRegister()


// Trait

class Trait(private val sortKey: String) : Comparable<Trait> {
    override fun compareTo(other: Trait): Int {
        (this.sortKey cmp other.sortKey).let { if (it != 0) return it }
        (this.getIdentifier() cmp other.getIdentifier()).let { if (it != 0) return it }
        return 0
    }
}


// TraitEffect

class TraitEffectKey


// TraitStack

class TraitStack(val trait: Trait, val level: Int) {
    init {
        require(level >= 1)
    }
}

fun NbtCompound.toTraitStack(): TraitStack? {
    val trait = this.getString("Trait").toIdentifier().toTrait() ?: return null
    val level = this.getInt("Level").takeIf { it >= 1 } ?: return null
    return TraitStack(trait, level)
}

fun TraitStack.toNbt(): NbtCompound {
    val nbt = NbtCompound()
    nbt.putString("Trait", this.trait.getIdentifier().string)
    nbt.putInt("Level", this.level)
    return nbt
}


// TraitStacks

class TraitStacks private constructor(val traitStackMap: SortedMap<Trait, Int>) {
    companion object {
        fun of(traitStackList: List<TraitStack>): TraitStacks {
            // 同じ特性をまとめて、各レベルをビットORする
            val traitStackMap = traitStackList
                .groupBy { it.trait }
                .mapValues {
                    it.value
                        .map { traitStack -> traitStack.level }
                        .reduce { a, b -> a or b }
                }
                .toSortedMap()
            return TraitStacks(traitStackMap)
        }

        fun readFromNbt(parent: NbtCompound, key: String = "TraitStacks"): TraitStacks? {
            if (!parent.contains(key, NbtElement.LIST_TYPE.toInt())) return null
            return parent.getList(key, NbtElement.COMPOUND_TYPE.toInt()).toTraitStacks()
        }
    }

    init {
        traitStackMap.forEach { (_, level) ->
            require(level >= 1)
        }
    }
}

fun NbtList.toTraitStacks(): TraitStacks {
    val traitStackList = (0..<this.size).mapNotNull {
        this.getCompound(it).toTraitStack()
    }
    return TraitStacks.of(traitStackList)
}

fun TraitStacks.toNbt(): NbtList {
    val nbt = NbtList()
    this.traitStackMap.forEach {
        nbt.add(TraitStack(it.key, it.value).toNbt())
    }
    return nbt
}
