package miragefairy2024.util.init

import miragefairy2024.MirageFairy2024DataGenerator
import net.minecraft.block.Block
import net.minecraft.item.Item

fun en(getter: () -> Pair<String, String>) {
    MirageFairy2024DataGenerator.englishTranslations += {
        val pair = getter()
        it.add(pair.first, pair.second)
    }
}

fun ja(getter: () -> Pair<String, String>) {
    MirageFairy2024DataGenerator.japaneseTranslations += {
        val pair = getter()
        it.add(pair.first, pair.second)
    }
}

fun Block.enJa(enName: String, jaName: String) {
    en { this.translationKey to enName }
    ja { this.translationKey to jaName }
}

fun Item.enJa(enName: String, jaName: String) {
    en { this.translationKey to enName }
    ja { this.translationKey to jaName }
}
