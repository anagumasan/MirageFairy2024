package miragefairy2024.mod

import miragefairy2024.MirageFairy2024
import miragefairy2024.util.enJa
import miragefairy2024.util.register
import miragefairy2024.util.registerGeneratedItemModelGeneration
import miragefairy2024.util.registerItemGroup
import net.minecraft.item.Item
import net.minecraft.util.Identifier

enum class MaterialCard(
    path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {
    FAIRY_PLASTIC(
        "fairy_plastic", "Fairy Plastic", "妖精のプラスチック",
        listOf(Poem("Thermoplastic organic polymer", "凍てつく記憶の宿る石。")),
    ),
    XARPITE(
        "xarpite", "Xarpite", "紅天石",
        listOf(Poem("Binds astral flux with magnetic force", "黒鉄の鎖は繋がれる。血腥い魂の檻へ。")),
    ),
    MIRANAGITE(
        "miranagite", "Miranagite", "蒼天石",
        listOf(Poem("Astral body crystallized by anti-entropy", "秩序の叛乱、天地創造の逆光。")),
    ),
    FAIRY_RUBBER(
        "fairy_rubber", "Fairy Rubber", "夜のかけら",
        listOf(Poem("Minimize the risk of losing belongings", "空は怯える夜精に一握りの温かい闇を与えた")),
    ),
    ;

    val identifier = Identifier(MirageFairy2024.modId, path)
    val item = Item(Item.Settings())
}

fun initMaterialsModule() {
    MaterialCard.entries.forEach { card ->
        card.item.register(card.identifier)
        card.item.registerItemGroup(mirageFairy2024ItemGroup)
        card.item.registerGeneratedItemModelGeneration()
        card.item.enJa(card.enName, card.jaName)
        card.item.registerPoem(card.poemList)
        card.item.registerPoemGeneration(card.poemList)
    }
}
