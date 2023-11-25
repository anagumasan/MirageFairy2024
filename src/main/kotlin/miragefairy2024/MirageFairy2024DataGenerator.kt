package miragefairy2024

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.item.Item
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
import java.util.concurrent.CompletableFuture

object MirageFairy2024DataGenerator : DataGeneratorEntrypoint {

    val blockStateModelGenerations = mutableListOf<(BlockStateModelGenerator) -> Unit>()
    val itemModelGenerations = mutableListOf<(ItemModelGenerator) -> Unit>()
    val blockTagGenerations = mutableListOf<((TagKey<Block>) -> FabricTagProvider<Block>.FabricTagBuilder) -> Unit>()
    val itemTagGenerations = mutableListOf<((TagKey<Item>) -> FabricTagProvider<Item>.FabricTagBuilder) -> Unit>()
    val blockLootTableGenerations = mutableListOf<(FabricBlockLootTableProvider) -> Unit>()
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
        pack.addProvider { output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup> ->
            object : FabricTagProvider.BlockTagProvider(output, registriesFuture) {
                override fun configure(arg: RegistryWrapper.WrapperLookup) {
                    blockTagGenerations.forEach {
                        it { tag -> getOrCreateTagBuilder(tag) }
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup> ->
            object : FabricTagProvider.ItemTagProvider(output, registriesFuture) {
                override fun configure(arg: RegistryWrapper.WrapperLookup) {
                    itemTagGenerations.forEach {
                        it { tag -> getOrCreateTagBuilder(tag) }
                    }
                }
            }
        }
        pack.addProvider { output: FabricDataOutput ->
            object : FabricBlockLootTableProvider(output) {
                override fun generate() {
                    blockLootTableGenerations.forEach {
                        it(this)
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
