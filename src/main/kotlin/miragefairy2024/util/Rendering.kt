package miragefairy2024.util

import miragefairy2024.MirageFairy2024
import net.minecraft.block.Block

fun Block.registerCutoutRenderLayer() {
    MirageFairy2024.onClientInit += {
        it.registerCutoutRenderLayer(this)
    }
}
