package miragefairy2024

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models

object MirageFairy2024DataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider { output: FabricDataOutput ->
            object : FabricModelProvider(output) {
                override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {

                }

                override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
                    itemModelGenerator.register(MirageFairy2024.fairyPlasticItem, Models.GENERATED)
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricLanguageProvider(output, "en_us") {
                override fun generateTranslations(translationBuilder: TranslationBuilder) {
                    translationBuilder.add(MirageFairy2024.fairyPlasticItem, "Fairy Plastic")
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricLanguageProvider(output, "ja_jp") {
                override fun generateTranslations(translationBuilder: TranslationBuilder) {
                    translationBuilder.add(MirageFairy2024.fairyPlasticItem, "妖精のプラスチック")
                }
            }
        }
    }
}
