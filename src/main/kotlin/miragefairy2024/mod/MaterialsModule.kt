package miragefairy2024.mod

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.init.enJa
import miragefairy2024.util.init.registerGeneratedItemModelGeneration
import miragefairy2024.util.init.registerItem
import miragefairy2024.util.init.registerItemGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.util.Identifier

enum class MaterialCard(
    val path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {
    FAIRY_PLASTIC(
        "fairy_plastic", "Fairy Plastic", "妖精のプラスチック",
        listOf(Poem("Thermoplastic organic polymer", "凍てつく記憶の宿る石。")),
    ),
    ;

    val item = Item(Item.Settings())
}

fun initMaterialsModule() {
    MaterialCard.entries.forEach { card ->
        card.item.registerItem(Identifier(MirageFairy2024.modId, card.path))
        card.item.registerItemGroup(ItemGroups.INGREDIENTS)
        card.item.registerGeneratedItemModelGeneration()
        card.item.enJa(card.enName, card.jaName)
        card.item.registerPoem(card.poemList)
        card.item.registerPoemGeneration(card.poemList)
    }
}
