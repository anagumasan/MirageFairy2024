package dummy12345678

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object DUMMY12345678 : ModInitializer {
    private val logger = LoggerFactory.getLogger("dummy12345678")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
	}
}