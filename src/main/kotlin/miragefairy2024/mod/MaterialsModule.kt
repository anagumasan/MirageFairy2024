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
) {
    FAIRY_PLASTIC("fairy_plastic", "Fairy Plastic", "妖精のプラスチック"),
    ;

    val item = Item(Item.Settings())
}

fun initMaterialsModule() {
    MaterialCard.entries.forEach { card ->
        card.item.registerItem(Identifier(MirageFairy2024.modId, card.path))
        card.item.registerItemGroup(ItemGroups.INGREDIENTS)
        card.item.registerGeneratedItemModelGeneration()
        card.item.enJa(card.enName, card.jaName)
    }
}
