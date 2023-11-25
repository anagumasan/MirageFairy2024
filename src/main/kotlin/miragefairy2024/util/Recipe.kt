package miragefairy2024.util

import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.minecraft.block.Blocks
import net.minecraft.block.ComposterBlock
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.loot.condition.LocationCheckLootCondition
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.loot.function.ExplosionDecayLootFunction
import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.registry.RegistryKey
import net.minecraft.world.biome.Biome

fun Item.registerGrassDrop(amount: Float = 1.0F, biome: (() -> RegistryKey<Biome>)? = null) {
    LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
        if (source.isBuiltin) {
            if (id == Blocks.GRASS.lootTableId) {
                tableBuilder.configure {
                    pool(LootPool(AlternativeLootPoolEntry {
                        alternatively(EmptyLootPoolEntry {
                            conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.SHEARS)))
                        })
                        alternatively(ItemLootPoolEntry(this@registerGrassDrop) {
                            conditionally(RandomChanceLootCondition.builder(0.125F * amount))
                            if (biome != null) conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(biome())))
                            apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                            apply(ExplosionDecayLootFunction.builder())
                        })
                    }))
                }
            }
        }
    }
}

fun Item.registerComposterInput(chance: Float) {
    ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(this, chance)
}
