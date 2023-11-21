package miragefairy2024

import miragefairy2024.util.init.configureGeneratedItemModelGeneration
import miragefairy2024.util.init.configureItemGroup
import miragefairy2024.util.init.enJa
import miragefairy2024.util.init.registerItem
import net.fabricmc.api.ModInitializer
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object MirageFairy2024 : ModInitializer {
    private val logger = LoggerFactory.getLogger("miragefairy2024")

    val fairyPlasticItem = Item(Item.Settings())

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")
        fairyPlasticItem.registerItem(Identifier("miragefairy2024", "fairy_plastic"))
        fairyPlasticItem.configureItemGroup(ItemGroups.INGREDIENTS)
        fairyPlasticItem.configureGeneratedItemModelGeneration()
        fairyPlasticItem.enJa("Fairy Plastic", "妖精のプラスチック")
    }
}
