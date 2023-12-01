package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import net.minecraft.util.Identifier

// TraitCard

enum class TraitCard(
    path: String,
) {
    ETHER_RESPIRATION("ether_respiration"),
    PHOTOSYNTHESIS("photosynthesis"),
    PHAEOSYNTHESIS("phaeosynthesis"),
    OSMOTIC_ABSORPTION("osmotic_absorption"),
    CRYSTAL_ABSORPTION("crystal_absorption"),

    AIR_ADAPTATION("air_adaptation"),
    COLD_ADAPTATION("cold_adaptation"),
    WARM_ADAPTATION("warm_adaptation"),
    HOT_ADAPTATION("hot_adaptation"),
    ARID_ADAPTATION("arid_adaptation"),
    MESIC_ADAPTATION("mesic_adaptation"),
    HUMID_ADAPTATION("humid_adaptation"),

    SEEDS_PRODUCTION("seeds_production"),
    FRUITS_PRODUCTION("fruits_production"),
    LEAVES_PRODUCTION("leaves_production"),
    EXPERIENCE_PRODUCTION("experience_production"),

    FAIRY_BLESSING("fairy_blessing"),

    FOUR_LEAFED("four_leafed"),
    NODED_STEM("noded_stem"),
    FRUIT_OF_KNOWLEDGE("fruit_of_knowledge"),
    GOLDEN_APPLE("golden_apple"),
    SPINY_LEAVES("spiny_leaves"),
    DESERT_GEM("desert_gem"),
    HEATING_MECHANISM("heating_mechanism"),
    WATERLOGGING_TOLERANCE("waterlogging_tolerance"),
    ADVERSITY_FLOWER("adversity_flower"),
    FLESHY_LEAVES("fleshy_leaves"),
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val trait = Trait()
}
