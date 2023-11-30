package miragefairy2024

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

object MirageFairy2024Client : ClientModInitializer {
    override fun onInitializeClient() {
        val clientProxy = object : ClientProxy {
            override fun registerItemTooltipCallback(block: (stack: ItemStack, lines: MutableList<Text>) -> Unit) {
                ItemTooltipCallback.EVENT.register { stack, _, lines ->
                    block(stack, lines)
                }
            }

            override fun registerCutoutRenderLayer(block: Block) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout())
            }

            override fun getClientPlayer(): PlayerEntity? = MinecraftClient.getInstance().player
        }
        MirageFairy2024.clientProxy = clientProxy
        MirageFairy2024.onClientInit.forEach {
            it(clientProxy)
        }
    }
}
