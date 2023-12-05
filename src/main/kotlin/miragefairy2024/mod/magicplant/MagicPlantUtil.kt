package miragefairy2024.mod.magicplant

import miragefairy2024.mod.mirageFairy2024ItemGroup
import miragefairy2024.mod.registerPoem
import miragefairy2024.mod.registerPoemGeneration
import miragefairy2024.util.enJa
import miragefairy2024.util.register
import miragefairy2024.util.registerComposterInput
import miragefairy2024.util.registerCutoutRenderLayer
import miragefairy2024.util.registerGeneratedItemModelGeneration
import miragefairy2024.util.registerItemGroup
import net.minecraft.block.Block

fun MagicPlantCard<*, *>.initMagicPlant() {

    // 登録
    block.register(blockIdentifier)
    blockEntityType.register(blockIdentifier)
    item.register(itemIdentifier)

    // 分類
    item.registerItemGroup(mirageFairy2024ItemGroup)

    // 見た目
    block.registerCutoutRenderLayer()
    item.registerGeneratedItemModelGeneration()

    // 翻訳
    block.enJa(blockEnName, blockJaName)
    item.enJa(itemEnName, itemJaName)
    item.registerPoem(seedPoemList)
    item.registerPoemGeneration(seedPoemList)

    // 性質
    //block.registerTagGenerate(BlockTags.SMALL_FLOWERS) // これをやるとエンダーマンが勝手に引っこ抜いていく

    // レシピ
    item.registerComposterInput(0.3F) // 種はコンポスターに投入可能

}

class WorldGenTraitRecipeInitScope(val block: Block) {

    @Suppress("FunctionName")
    fun A(levelString: String, traitCard: TraitCard, condition: WorldGenTraitRecipe.Condition = WorldGenTraitRecipe.Condition.Always) {
        registerWorldGenTraitRecipe(WorldGenTraitRecipe(block, WorldGenTraitRecipe.Rarity.A, traitCard.trait, levelString.toInt(2), condition))
    }

    @Suppress("FunctionName")
    fun N(levelString: String, traitCard: TraitCard, condition: WorldGenTraitRecipe.Condition = WorldGenTraitRecipe.Condition.Always) {
        registerWorldGenTraitRecipe(WorldGenTraitRecipe(block, WorldGenTraitRecipe.Rarity.N, traitCard.trait, levelString.toInt(2), condition))
    }

    @Suppress("FunctionName")
    fun R(levelString: String, traitCard: TraitCard, condition: WorldGenTraitRecipe.Condition = WorldGenTraitRecipe.Condition.Always) {
        registerWorldGenTraitRecipe(WorldGenTraitRecipe(block, WorldGenTraitRecipe.Rarity.R, traitCard.trait, levelString.toInt(2), condition))
    }

    @Suppress("FunctionName")
    fun S(levelString: String, traitCard: TraitCard, condition: WorldGenTraitRecipe.Condition = WorldGenTraitRecipe.Condition.Always) {
        registerWorldGenTraitRecipe(WorldGenTraitRecipe(block, WorldGenTraitRecipe.Rarity.S, traitCard.trait, levelString.toInt(2), condition))
    }

}
