package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import net.minecraft.util.Identifier

// TraitCard

enum class TraitCard(
    path: String,
    sortKey: String,
) {
    ETHER_RESPIRATION("ether_respiration", "0nutrition"),
    PHOTOSYNTHESIS("photosynthesis", "0nutrition"),
    PHAEOSYNTHESIS("phaeosynthesis", "0nutrition"),
    OSMOTIC_ABSORPTION("osmotic_absorption", "0nutrition"),
    CRYSTAL_ABSORPTION("crystal_absorption", "0nutrition"),

    AIR_ADAPTATION("air_adaptation", "1atmosphere"),
    COLD_ADAPTATION("cold_adaptation", "3biome"),
    WARM_ADAPTATION("warm_adaptation", "3biome"),
    HOT_ADAPTATION("hot_adaptation", "3biome"),
    ARID_ADAPTATION("arid_adaptation", "3biome"),
    MESIC_ADAPTATION("mesic_adaptation", "3biome"),
    HUMID_ADAPTATION("humid_adaptation", "3biome"),

    SEEDS_PRODUCTION("seeds_production", "4production"),
    FRUITS_PRODUCTION("fruits_production", "4production"),
    LEAVES_PRODUCTION("leaves_production", "4production"),
    EXPERIENCE_PRODUCTION("experience_production", "4production"),

    FAIRY_BLESSING("fairy_blessing", "5skill"),

    FOUR_LEAFED("four_leafed", "6part"),
    NODED_STEM("noded_stem", "6part"),
    FRUIT_OF_KNOWLEDGE("fruit_of_knowledge", "6part"),
    GOLDEN_APPLE("golden_apple", "6part"),
    SPINY_LEAVES("spiny_leaves", "6part"),
    DESERT_GEM("desert_gem", "6part"),
    HEATING_MECHANISM("heating_mechanism", "6part"),
    WATERLOGGING_TOLERANCE("waterlogging_tolerance", "6part"),
    ADVERSITY_FLOWER("adversity_flower", "6part"),
    FLESHY_LEAVES("fleshy_leaves", "6part"),
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val trait = Trait(sortKey)
}


// TraitEffectKey

enum class TraitEffectKeyCard(
    path: String,
) {
    NUTRITION("nutrition"),
    ENVIRONMENT("environment"),
    GROWTH_BOOST("growth_boost"),
    SEEDS_PRODUCTION("seeds_production"),
    FRUITS_PRODUCTION("fruits_production"),
    LEAVES_PRODUCTION("leaves_production"),
    PRODUCTION_BOOST("production_boost"),
    EXPERIENCE_PRODUCTION("experience_production"),
    FORTUNE_FACTOR("fortune_factor"),
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val traitEffectKey = TraitEffectKey()
}
