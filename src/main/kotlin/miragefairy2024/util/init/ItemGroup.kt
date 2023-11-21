package miragefairy2024.util.init

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey

fun Item.configureItemGroup(itemGroup: RegistryKey<ItemGroup>) {
    ItemGroupEvents.modifyEntriesEvent(itemGroup).register {
        it.add(this)
    }
}
