package miragefairy2024.mod.magicplant

import miragefairy2024.util.bitCount
import miragefairy2024.util.en
import miragefairy2024.util.ja
import miragefairy2024.util.text
import mirrg.kotlin.hydrogen.unit
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.Util

// Trait

fun Trait.register(identifier: Identifier) = unit { Registry.register(traitRegistry, identifier, this) }

fun Trait.getIdentifier() = traitRegistry.getId(this)!!
fun Identifier.toTrait() = traitRegistry.get(this)

fun Trait.getTranslationKey(): String = Util.createTranslationKey("mirageFairy2024.trait", this.getIdentifier())
fun Trait.getName() = run { text { translate(this@run.getTranslationKey()) } }

fun Trait.enJa(enName: String, jaName: String) {
    en { this.getTranslationKey() to enName }
    ja { this.getTranslationKey() to jaName }
}


// TraitEffects

operator fun MutableTraitEffects.plusAssign(other: MutableTraitEffects) {
    other.keys.forEach { key ->
        fun <T : Any> f(key: TraitEffectKey<T>) {
            this[key] = key.plus(this[key], other[key])
        }
        f(key)
    }
}


// TraitEffect

fun <T : Any> TraitEffect<T>.getDescription() = this.key.getDescription(this.value)


// TraitEffectKey

fun TraitEffectKey<*>.register(identifier: Identifier) = unit { Registry.register(traitEffectKeyRegistry, identifier, this) }

fun TraitEffectKey<*>.getIdentifier() = traitEffectKeyRegistry.getId(this)!!
fun Identifier.toTraitEffectKey() = traitEffectKeyRegistry.get(this)

fun TraitEffectKey<*>.getTranslationKey(): String = Util.createTranslationKey("mirageFairy2024.traitEffect", this.getIdentifier())
fun TraitEffectKey<*>.getName() = run { text { translate(this@run.getTranslationKey()) } }

fun TraitEffectKey<*>.enJa(enName: String, jaName: String) {
    en { this.getTranslationKey() to enName }
    ja { this.getTranslationKey() to jaName }
}


// TraitStacks

fun ItemStack.getTraitStacks(): TraitStacks? {
    val nbt = this.nbt ?: return null
    return TraitStacks.readFromNbt(nbt)
}

val TraitStacks.bitCount get() = this.traitStackList.sumOf { it.level.bitCount }
