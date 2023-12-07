package miragefairy2024.mod

import miragefairy2024.MirageFairy2024
import miragefairy2024.MirageFairy2024DataGenerator
import miragefairy2024.util.criterion
import miragefairy2024.util.enJa
import miragefairy2024.util.getIdentifier
import miragefairy2024.util.group
import miragefairy2024.util.register
import miragefairy2024.util.registerGeneratedItemModelGeneration
import miragefairy2024.util.registerItemGroup
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.util.Identifier

enum class MaterialCard(
    path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {

    FAIRY_PLASTIC(
        // TODO add purpose
        "fairy_plastic", "Fairy Plastic", "妖精のプラスチック",
        listOf(Poem("Thermoplastic organic polymer", "凍てつく記憶の宿る石。")),
    ),
    XARPITE(
        // TODO add purpose
        "xarpite", "Xarpite", "紅天石",
        listOf(Poem("Binds astral flux with magnetic force", "黒鉄の鎖は繋がれる。血腥い魂の檻へ。")),
    ),
    MIRANAGITE(
        // TODO add purpose
        "miranagite", "Miranagite", "蒼天石",
        listOf(Poem("Astral body crystallized by anti-entropy", "秩序の叛乱、天地創造の逆光。")),
    ),
    FAIRY_RUBBER(
        // TODO add purpose
        "fairy_rubber", "Fairy Rubber", "夜のかけら",
        listOf(Poem("Minimize the risk of losing belongings", "空は怯える夜精に一握りの温かい闇を与えた")),
    ),

    MIRAGE_LEAVES(
        "mirage_leaves", "Mirage Leaves", "ミラージュの葉",
        listOf(Poem("Don't cut your fingers!", "刻まれる、記憶の破片。")),
    ),
    VEROPEDA_LEAF(
        // TODO add purpose
        "veropeda_leaf", "Veropeda Leaf", "ヴェロペダの葉",
        listOf(Poem("Said to house the soul of a demon", "その身融かされるまでの快楽。")),
    ),
    VEROPEDA_BERRIES(
        // TODO add purpose
        "veropeda_berries", "Veropeda Berries", "ヴェロペダの実",
        listOf(Poem("Has analgesic and stimulant effects", "悪魔の囁きを喰らう。")),
    ),

    TINY_MIRAGE_FLOUR(
        "tiny_mirage_flour", "Tiny Pile of Mirage Flour", "小さなミラージュの花粉",
        listOf(Poem("Compose the body of Mirage fairy", "ささやかな温もりを、てのひらの上に。")),
    ),
    MIRAGE_FLOUR(
        "mirage_flour", "Mirage Flour", "ミラージュの花粉",
        listOf(Poem("Containing metallic organic matter", "叡智の根源、創発のファンタジア。")),
    ),
    RARE_MIRAGE_FLOUR(
        "rare_mirage_flour", "Rare Mirage Flour", "高純度ミラージュの花粉",
        listOf(Poem("Use the difference in ether resistance", "艶やかなほたる色に煌めく鱗粉。")),
    ),
    VERY_RARE_MIRAGE_FLOUR(
        "very_rare_mirage_flour", "Very Rare Mirage Flour", "特選高純度ミラージュの花粉",
        listOf(Poem("As intelligent as humans", "黄金の魂が示す、好奇心の輝き。")),
    ),
    ULTRA_RARE_MIRAGE_FLOUR(
        "ultra_rare_mirage_flour", "Ultra Rare Mirage Flour", "厳選高純度ミラージュの花粉",
        listOf(Poem("Awaken fairies in the world and below", "1,300ケルビンの夜景。")),
    ),
    SUPER_RARE_MIRAGE_FLOUR(
        "super_rare_mirage_flour", "Super Rare Mirage Flour", "激甚高純度ミラージュの花粉",
        listOf(Poem("Explore atmosphere and nearby universe", "蒼淵を彷徨う影、導きの光。")),
    ),
    EXTREMELY_RARE_MIRAGE_FLOUR(
        "extremely_rare_mirage_flour", "Extremely Rare Mirage Flour", "極超高純度ミラージュの花粉",
        listOf(
            Poem("poem1", "Leap spaces by collapsing time crystals,", "運命の束、時の結晶、光速の呪いを退けよ、"),
            Poem("poem2", "capture ether beyond observable universe", "讃えよ、アーカーシャに眠る自由の頂きを。"),
        ),
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

    fun registerCompressionRecipeGeneration(low: MaterialCard, high: MaterialCard) = MirageFairy2024DataGenerator.recipeGenerators {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.MISC, high.item, 1)
            .group(high.item)
            .input('#', low.item)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .criterion(low.item)
            .offerTo(it, Identifier.of(MirageFairy2024.modId, "${high.item.getIdentifier().path}_from_${low.item.getIdentifier().path}"))
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.MISC, low.item, 9)
            .group(low.item)
            .input(high.item)
            .criterion(high.item)
            .offerTo(it, Identifier.of(MirageFairy2024.modId, "${low.item.getIdentifier().path}_from_${high.item.getIdentifier().path}"))
    }
    registerCompressionRecipeGeneration(MaterialCard.TINY_MIRAGE_FLOUR, MaterialCard.MIRAGE_FLOUR)
    registerCompressionRecipeGeneration(MaterialCard.MIRAGE_FLOUR, MaterialCard.RARE_MIRAGE_FLOUR)
    registerCompressionRecipeGeneration(MaterialCard.RARE_MIRAGE_FLOUR, MaterialCard.VERY_RARE_MIRAGE_FLOUR)
    registerCompressionRecipeGeneration(MaterialCard.VERY_RARE_MIRAGE_FLOUR, MaterialCard.ULTRA_RARE_MIRAGE_FLOUR)
    registerCompressionRecipeGeneration(MaterialCard.ULTRA_RARE_MIRAGE_FLOUR, MaterialCard.SUPER_RARE_MIRAGE_FLOUR)
    registerCompressionRecipeGeneration(MaterialCard.SUPER_RARE_MIRAGE_FLOUR, MaterialCard.EXTREMELY_RARE_MIRAGE_FLOUR)

}
