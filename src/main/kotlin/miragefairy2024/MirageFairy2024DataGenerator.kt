package miragefairy2024

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator

object MirageFairy2024DataGenerator : DataGeneratorEntrypoint {

    val blockStateModelGenerations = mutableListOf<(BlockStateModelGenerator) -> Unit>()
    val itemModelGenerations = mutableListOf<(ItemModelGenerator) -> Unit>()
    val englishTranslations = mutableListOf<(FabricLanguageProvider.TranslationBuilder) -> Unit>()
    val japaneseTranslations = mutableListOf<(FabricLanguageProvider.TranslationBuilder) -> Unit>()

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider { output: FabricDataOutput ->
            object : FabricModelProvider(output) {
                override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
                    blockStateModelGenerations.forEach {
                        it(blockStateModelGenerator)
                    }
                }

                override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
                    itemModelGenerations.forEach {
                        it(itemModelGenerator)
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricLanguageProvider(output, "en_us") {
                override fun generateTranslations(translationBuilder: TranslationBuilder) {
                    englishTranslations.forEach {
                        it(translationBuilder)
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricLanguageProvider(output, "ja_jp") {
                override fun generateTranslations(translationBuilder: TranslationBuilder) {
                    japaneseTranslations.forEach {
                        it(translationBuilder)
                    }
                }
            }
        }
    }
}
