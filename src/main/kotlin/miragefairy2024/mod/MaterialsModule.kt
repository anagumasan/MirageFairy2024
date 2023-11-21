package miragefairy2024.mod

import miragefairy2024.util.init.configureGeneratedItemModelGeneration
import miragefairy2024.util.init.configureItemGroup
import miragefairy2024.util.init.enJa
import miragefairy2024.util.init.registerItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.util.Identifier

val fairyPlasticItem = Item(Item.Settings())

fun initMaterialsModule() {
    fairyPlasticItem.registerItem(Identifier("miragefairy2024", "fairy_plastic"))
    fairyPlasticItem.configureItemGroup(ItemGroups.INGREDIENTS)
    fairyPlasticItem.configureGeneratedItemModelGeneration()
    fairyPlasticItem.enJa("Fairy Plastic", "妖精のプラスチック")
}
