package miragefairy2024.mod.magicplant

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.PlantBlock
import net.minecraft.item.AliasedBlockItem

abstract class MagicPlantBlock(settings: Settings) : PlantBlock(settings), BlockEntityProvider

class MagicPlantSeedItem(block: Block, settings: Settings) : AliasedBlockItem(block, settings)
