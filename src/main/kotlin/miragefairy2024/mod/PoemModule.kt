package miragefairy2024.mod

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.formatted
import miragefairy2024.util.init.en
import miragefairy2024.util.init.ja
import miragefairy2024.util.text
import net.minecraft.item.Item
import net.minecraft.util.Formatting

fun initPoemModule() {
    MirageFairy2024.onClientInit += {
        it.registerItemTooltipCallback { stack, lines ->
            val poemList = itemPoemListTable[stack.item] ?: return@registerItemTooltipCallback
            poemList.forEachIndexed { index, poem ->
                lines.add(1 + index, text { translate("${stack.item.translationKey}.${poem.key}").formatted(poem.color) })
            }
        }
    }
}


class Poem(val key: String, val en: String, val ja: String, val color: Formatting)

fun Poem(en: String, ja: String) = Poem("poem", en, ja, Formatting.DARK_AQUA)


val itemPoemListTable = mutableMapOf<Item, List<Poem>>()

fun Item.registerPoem(poemList: List<Poem>) {
    require(this !in itemPoemListTable)
    itemPoemListTable[this] = poemList
}


fun Item.registerPoemGeneration(poemList: List<Poem>) {
    poemList.forEach {
        en { "${this.translationKey}.${it.key}" to it.en }
        ja { "${this.translationKey}.${it.key}" to it.ja }
    }
}
