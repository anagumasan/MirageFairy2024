package miragefairy2024

import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import miragefairy2024.mod.magicplant.WorldGenTraitRecipe
import miragefairy2024.mod.magicplant.getName
import miragefairy2024.mod.magicplant.magicplants.MirageFlowerCard
import miragefairy2024.mod.magicplant.worldGenTraitRecipeRegistry
import miragefairy2024.util.createItemStack
import miragefairy2024.util.formatted
import miragefairy2024.util.invoke
import miragefairy2024.util.plus
import miragefairy2024.util.text
import miragefairy2024.util.toEntryStack
import net.minecraft.text.Text

@Suppress("unused")
class MirageFairy2024ReiClientPlugin : REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(WorldGenTraitCategory())
    }

    override fun registerDisplays(registry: DisplayRegistry) {

        worldGenTraitRecipeRegistry.values.flatten().forEach { recipe ->
            registry.add(WorldGenTraitDisplay(recipe))
        }

    }
}


class WorldGenTraitCategory : DisplayCategory<WorldGenTraitDisplay> {
    override fun getCategoryIdentifier() = WorldGenTraitDisplay.IDENTIFIER
    override fun getTitle(): Text = WorldGenTraitDisplay.TRANSLATION()
    override fun getIcon(): Renderer = MirageFlowerCard.item.createItemStack().toEntryStack()
    override fun getDisplayWidth(display: WorldGenTraitDisplay) = 180
    override fun getDisplayHeight() = 36
    override fun setupDisplay(display: WorldGenTraitDisplay, bounds: Rectangle): List<Widget> {
        val rarityText = when (display.recipe.rarity) {
            WorldGenTraitRecipe.Rarity.A -> text { "100%"() }
            WorldGenTraitRecipe.Rarity.C -> text { ">99%"() }
            WorldGenTraitRecipe.Rarity.N -> text { "<90%"() }
            WorldGenTraitRecipe.Rarity.R -> text { "<8%"() }
            WorldGenTraitRecipe.Rarity.S -> text { "<1%"() }
        }
        val traitStackText = text { (display.recipe.trait.getName() + " "() + display.recipe.level.toString(2)()).formatted(display.recipe.trait.color) }
        val p = bounds.location + Point(3, 3)
        return listOf(
            Widgets.createRecipeBase(bounds),

            Widgets.createResultSlotBackground(p + Point(15 - 8, 15 - 8)), // 出力スロット背景
            Widgets.createSlot(p + Point(15 - 8, 15 - 8)).entries(display.outputEntries[0]).disableBackground().markOutput(), // 出力アイテム

            Widgets.createLabel(p + Point(50, 5), rarityText).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow(), // 出現頻度
            Widgets.createLabel(p + Point(70, 5), display.recipe.condition.description).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow().leftAligned(), // 条件
            Widgets.createLabel(p + Point(30, 17), traitStackText).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow().leftAligned(), // 特性
        )
    }
}
