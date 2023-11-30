package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import miragefairy2024.mod.Poem
import miragefairy2024.mod.mirageFairy2024ItemGroup
import miragefairy2024.mod.registerPoem
import miragefairy2024.mod.registerPoemGeneration
import miragefairy2024.util.BlockStateVariant
import miragefairy2024.util.concat
import miragefairy2024.util.enJa
import miragefairy2024.util.register
import miragefairy2024.util.registerComposterInput
import miragefairy2024.util.registerCutoutRenderLayer
import miragefairy2024.util.registerGeneratedItemModelGeneration
import miragefairy2024.util.registerItemGroup
import miragefairy2024.util.registerModelGeneration
import miragefairy2024.util.registerVariantsBlockStateGeneration
import miragefairy2024.util.with
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.BlockState
import net.minecraft.block.MapColor
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class MagicPlantCard<B : MagicPlantBlock, BE : BlockEntity>(
    blockPath: String,
    val blockEnName: String,
    val blockJaName: String,
    itemPath: String,
    val itemEnName: String,
    val itemJaName: String,
    val seedPoemList: List<Poem>,
    blockCreator: () -> B,
    blockEntityCreator: (BlockPos, BlockState) -> BE,
) {
    companion object {
        val MIRAGE_FLOWER = MagicPlantCard(
            "mirage_flower", "Mirage Flower", "ミラージュの花",
            "mirage_bulb", "Mirage Bulb", "ミラージュの球根",
            listOf(
                Poem("Evolution to escape extermination", "可憐にして人畜無害たる魔物。"),
                Poem("classification", "Order Miragales, family Miragaceae", "妖花目ミラージュ科"),
            ),
            { MirageFlowerBlock(createCommonSettings().breakInstantly().mapColor(MapColor.DIAMOND_BLUE).sounds(BlockSoundGroup.GLASS)) },
            ::MirageFlowerBlockEntity,
        )

        private fun createCommonSettings(): FabricBlockSettings = FabricBlockSettings.create().noCollision().ticksRandomly().pistonBehavior(PistonBehavior.DESTROY)
    }

    val blockIdentifier = Identifier(MirageFairy2024.modId, blockPath)
    val itemIdentifier = Identifier(MirageFairy2024.modId, itemPath)
    val block = blockCreator()
    val blockEntityType = BlockEntityType(blockEntityCreator, setOf(block), null)
    val item = MagicPlantSeedItem(block, Item.Settings())
}

fun initMagicPlantModule() {

    fun init(card: MagicPlantCard<*, *>) {
        card.block.register(card.blockIdentifier)
        card.blockEntityType.register(card.blockIdentifier)
        card.item.register(card.itemIdentifier)

        card.item.registerItemGroup(mirageFairy2024ItemGroup)

        card.block.registerCutoutRenderLayer()
        card.item.registerGeneratedItemModelGeneration()

        card.block.enJa(card.blockEnName, card.blockJaName)
        card.item.enJa(card.itemEnName, card.itemJaName)
        card.item.registerPoem(card.seedPoemList)
        card.item.registerPoemGeneration(card.seedPoemList)

        card.item.registerComposterInput(0.3F) // 種はコンポスターに投入可能
    }

    MagicPlantCard.MIRAGE_FLOWER.let { card ->
        init(card)

        card.block.registerVariantsBlockStateGeneration {
            (0..MirageFlowerBlock.MAX_AGE).map { age ->
                listOf("age" to "$age") to BlockStateVariant("block/" concat card.blockIdentifier concat "_age$age")
            }
        }
        (0..MirageFlowerBlock.MAX_AGE).forEach { age ->
            val texturedModel = Models.CROSS.with(TextureKey.CROSS to ("block/" concat card.blockIdentifier concat "_age$age"))
            texturedModel.registerModelGeneration("block/" concat card.blockIdentifier concat "_age$age")
        }

        //card.block.registerTagGenerate(BlockTags.SMALL_FLOWERS) // これをやるとエンダーマンが勝手に引っこ抜いていく

        // TODO worldgen
    }

}
