package miragefairy2024

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
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
        Registry.register(Registries.ITEM, Identifier("miragefairy2024", "fairy_plastic"), fairyPlasticItem)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register {
            it.add(fairyPlasticItem)
        }
    }
}