package miragefairy2024.util.init

import miragefairy2024.MirageFairy2024DataGenerator
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

fun Item.en(enName: String) {
    MirageFairy2024DataGenerator.englishTranslations += {
        it.add(this, enName)
    }
}

fun Item.ja(jaName: String) {
    MirageFairy2024DataGenerator.japaneseTranslations += {
        it.add(this, jaName)
    }
}

fun Item.enJa(enName: String, jaName: String) {
    this.en(enName)
    this.ja(jaName)
}
