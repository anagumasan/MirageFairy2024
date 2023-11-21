package miragefairy2024

import miragefairy2024.mod.initMaterialsModule
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MirageFairy2024 : ModInitializer {
    val modId = "miragefairy2024"
    val logger = LoggerFactory.getLogger("miragefairy2024")

    val onClientInit = mutableListOf<(ClientProxy) -> Unit>()

    override fun onInitialize() {
        initMaterialsModule()
    }
}
