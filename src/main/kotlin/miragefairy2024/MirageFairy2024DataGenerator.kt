package miragefairy2024

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.item.Item
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
import java.util.concurrent.CompletableFuture

object MirageFairy2024DataGenerator : DataGeneratorEntrypoint {

    val blockStateModelGenerators = mutableListOf<(BlockStateModelGenerator) -> Unit>()
    val itemModelGenerators = mutableListOf<(ItemModelGenerator) -> Unit>()
    val blockTagGenerators = mutableListOf<((TagKey<Block>) -> FabricTagProvider<Block>.FabricTagBuilder) -> Unit>()
    val itemTagGenerators = mutableListOf<((TagKey<Item>) -> FabricTagProvider<Item>.FabricTagBuilder) -> Unit>()
    val blockLootTableGenerators = mutableListOf<(FabricBlockLootTableProvider) -> Unit>()
    val recipeGenerators = mutableListOf<(RecipeExporter) -> Unit>()
    val englishTranslationGenerators = mutableListOf<(FabricLanguageProvider.TranslationBuilder) -> Unit>()
    val japaneseTranslationGenerators = mutableListOf<(FabricLanguageProvider.TranslationBuilder) -> Unit>()

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider { output: FabricDataOutput ->
            object : FabricModelProvider(output) {
                override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
                    blockStateModelGenerators.forEach {
                        it(blockStateModelGenerator)
                    }
                }

                override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
                    itemModelGenerators.forEach {
                        it(itemModelGenerator)
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup> ->
            object : FabricTagProvider.BlockTagProvider(output, registriesFuture) {
                override fun configure(arg: RegistryWrapper.WrapperLookup) {
                    blockTagGenerators.forEach {
                        it { tag -> getOrCreateTagBuilder(tag) }
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup> ->
            object : FabricTagProvider.ItemTagProvider(output, registriesFuture) {
                override fun configure(arg: RegistryWrapper.WrapperLookup) {
                    itemTagGenerators.forEach {
                        it { tag -> getOrCreateTagBuilder(tag) }
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricBlockLootTableProvider(output) {
                override fun generate() {
                    blockLootTableGenerators.forEach {
                        it(this)
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricRecipeProvider(output) {
                override fun generate(exporter: RecipeExporter) {
                    recipeGenerators.forEach {
                        it(exporter)
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricLanguageProvider(output, "en_us") {
                override fun generateTranslations(translationBuilder: TranslationBuilder) {
                    englishTranslationGenerators.forEach {
                        it(translationBuilder)
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricLanguageProvider(output, "ja_jp") {
                override fun generateTranslations(translationBuilder: TranslationBuilder) {
                    japaneseTranslationGenerators.forEach {
                        it(translationBuilder)
                    }
                }
            }
        }
    }
}
