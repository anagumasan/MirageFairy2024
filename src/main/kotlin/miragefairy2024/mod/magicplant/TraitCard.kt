package miragefairy2024.mod.magicplant

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

// TraitCard

enum class TraitCard(
    path: String,
    sortKey: String,
    val enName: String,
    val jaName: String,
) {
    ETHER_RESPIRATION("ether_respiration", "0nutrition", "Ether Respiration", "エーテル呼吸"),
    PHOTOSYNTHESIS("photosynthesis", "0nutrition", "Photosynthesis", "光合成"),
    PHAEOSYNTHESIS("phaeosynthesis", "0nutrition", "Phaeosynthesis", "闇合成"),
    OSMOTIC_ABSORPTION("osmotic_absorption", "0nutrition", "Osmotic Absorption", "浸透吸収"),
    CRYSTAL_ABSORPTION("crystal_absorption", "0nutrition", "Crystal Absorption", "鉱物吸収"),

    AIR_ADAPTATION("air_adaptation", "1atmosphere", "Air Adaptation", "空気適応"),
    COLD_ADAPTATION("cold_adaptation", "3biome", "Cold Adaptation", "寒冷適応"),
    WARM_ADAPTATION("warm_adaptation", "3biome", "Warm Adaptation", "温暖適応"),
    HOT_ADAPTATION("hot_adaptation", "3biome", "Hot Adaptation", "熱帯適応"),
    ARID_ADAPTATION("arid_adaptation", "3biome", "Arid Adaptation", "乾燥適応"),
    MESIC_ADAPTATION("mesic_adaptation", "3biome", "Mesic Adaptation", "中湿適応"),
    HUMID_ADAPTATION("humid_adaptation", "3biome", "Humid Adaptation", "湿潤適応"),

    SEEDS_PRODUCTION("seeds_production", "4production", "Seeds Production", "種子生成"),
    FRUITS_PRODUCTION("fruits_production", "4production", "Fruits Production", "果実生成"),
    LEAVES_PRODUCTION("leaves_production", "4production", "Leaves Production", "葉面生成"),
    EXPERIENCE_PRODUCTION("experience_production", "4production", "Xp Production", "経験値生成"),

    FAIRY_BLESSING("fairy_blessing", "5skill", "Fairy Blessing", "妖精の祝福"),

    FOUR_LEAFED("four_leafed", "6part", "Four-leafed", "四つ葉"),
    NODED_STEM("noded_stem", "6part", "Noded Stem", "節状の茎"),
    FRUIT_OF_KNOWLEDGE("fruit_of_knowledge", "6part", "Fruit of Knowledge", "知識の果実"),
    GOLDEN_APPLE("golden_apple", "6part", "Golden Apple", "金のリンゴ"),
    SPINY_LEAVES("spiny_leaves", "6part", "Spiny Leaves", "棘状の葉"),
    DESERT_GEM("desert_gem", "6part", "Desert Gem", "砂漠の宝石"),
    HEATING_MECHANISM("heating_mechanism", "6part", "Heating Mechanism", "発熱機構"),
    WATERLOGGING_TOLERANCE("waterlogging_tolerance", "6part", "Waterlogging Tolerance", "浸水耐性"),
    ADVERSITY_FLOWER("adversity_flower", "6part", "Adversity Flower", "高嶺の花"),
    FLESHY_LEAVES("fleshy_leaves", "6part", "Fleshy Leaves", "肉厚の葉"),
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val trait = Trait(sortKey)
}


// TraitEffectKey

enum class TraitEffectKeyCard(
    path: String,
    val enName: String,
    val jaName: String,
    val color: Formatting,
) {
    NUTRITION("nutrition", "NTR", "栄養値", Formatting.AQUA),
    ENVIRONMENT("environment", "ENV", "環境値", Formatting.GREEN),
    GROWTH_BOOST("growth_boost", "GRW", "成長速度", Formatting.DARK_BLUE),
    SEEDS_PRODUCTION("seeds_production", "SEED", "種子生成", Formatting.GOLD),
    FRUITS_PRODUCTION("fruits_production", "FRUIT", "果実生成", Formatting.LIGHT_PURPLE),
    LEAVES_PRODUCTION("leaves_production", "LEAF", "葉面生成", Formatting.DARK_GREEN),
    PRODUCTION_BOOST("production_boost", "PRD", "生産能力", Formatting.DARK_RED),
    EXPERIENCE_PRODUCTION("experience_production", "XP", "経験値", Formatting.YELLOW),
    FORTUNE_FACTOR("fortune_factor", "FTN", "幸運係数", Formatting.DARK_PURPLE),
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val traitEffectKey = object : TraitEffectKey<Double>() {
        override fun getDescription(value: Double) = text { getName() + (value * 100 formatAs "%+.0f%%")() }
    }
}
