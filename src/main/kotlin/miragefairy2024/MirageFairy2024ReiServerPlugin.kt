package miragefairy2024

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import miragefairy2024.mod.magicplant.TraitStack
import miragefairy2024.mod.magicplant.TraitStacks
import miragefairy2024.mod.magicplant.WorldGenTraitRecipe
import miragefairy2024.mod.magicplant.getIdentifier
import miragefairy2024.mod.magicplant.setTraitStacks
import miragefairy2024.mod.magicplant.toTrait
import miragefairy2024.util.Translation
import miragefairy2024.util.createItemStack
import miragefairy2024.util.getIdentifier
import miragefairy2024.util.string
import miragefairy2024.util.toBlock
import miragefairy2024.util.toEntryIngredient
import miragefairy2024.util.toEntryStack
import miragefairy2024.util.toIdentifier
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("unused")
class MirageFairy2024ReiServerPlugin : REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(WorldGenTraitDisplay.IDENTIFIER, WorldGenTraitDisplay.SERIALIZER)
    }
}


class WorldGenTraitDisplay(val recipe: WorldGenTraitRecipe) : BasicDisplay(listOf(), recipe.getOutput()) {
    companion object {
        val IDENTIFIER: CategoryIdentifier<WorldGenTraitDisplay> by lazy { CategoryIdentifier.of(MirageFairy2024.modId, "plugins/world_gen_trait") }
        val TRANSLATION = Translation({ "category.rei.${MirageFairy2024.modId}.world_gen_trait" }, "World Gen Trait", "地形生成特性")
        val SERIALIZER: Serializer<WorldGenTraitDisplay> by lazy {
            Serializer.ofRecipeLess({ input, output, tag ->
                WorldGenTraitDisplay(
                    WorldGenTraitRecipe(
                        tag.getString("Block").toIdentifier().toBlock(),
                        WorldGenTraitRecipe.Rarity.valueOf(tag.getString("Rarity")),
                        tag.getString("Trait").toIdentifier().toTrait()!!,
                        tag.getInt("Level"),
                        object : WorldGenTraitRecipe.Condition {
                            override val description get() = Text.Serializer.fromJson(tag.getString("ConditionDescription"))!!
                            override fun canSpawn(world: World, blockPos: BlockPos) = throw UnsupportedOperationException()
                        },
                    )
                )
            }, { display, tag ->
                tag.putString("Block", display.recipe.block.getIdentifier().string)
                tag.putString("Rarity", display.recipe.rarity.name)
                tag.putString("Trait", display.recipe.trait.getIdentifier().string)
                tag.putInt("Level", display.recipe.level)
                tag.putString("ConditionDescription", Text.Serializer.toJson(display.recipe.condition.description))
            })
        }

        private fun WorldGenTraitRecipe.getOutput(): List<EntryIngredient> {
            val trait = TraitStack(this.trait, this.level)
            val itemStack = this.block.asItem().createItemStack().also { it.setTraitStacks(TraitStacks.of(listOf(trait))) }
            return listOf(itemStack.toEntryStack().toEntryIngredient())
        }
    }

    override fun getCategoryIdentifier() = IDENTIFIER
}
