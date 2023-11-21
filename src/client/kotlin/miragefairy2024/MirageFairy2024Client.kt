package miragefairy2024

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

object MirageFairy2024Client : ClientModInitializer {
    override fun onInitializeClient() {
        MirageFairy2024.onClientInit.forEach {
            it(object : ClientProxy {
                override fun registerItemTooltipCallback(block: (stack: ItemStack, lines: MutableList<Text>) -> Unit) {
                    ItemTooltipCallback.EVENT.register { stack, _, lines ->
                        block(stack, lines)
                    }
                }
            })
        }
    }
}
