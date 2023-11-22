package miragefairy2024.util

import com.google.gson.JsonElement
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Supplier

fun Model(creator: (TextureMap) -> JsonElement): Model = object : Model(Optional.empty(), Optional.empty()) {
    override fun upload(id: Identifier, textures: TextureMap, modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>): Identifier {
        modelCollector.accept(id) { creator(textures) }
        return id
    }
}

fun Model(parent: Identifier, vararg textureKeys: TextureKey) = Model(Optional.of(parent), Optional.empty(), *textureKeys)

fun Model(parent: Identifier, variant: String, vararg textureKeys: TextureKey) = Model(Optional.of(parent), Optional.of(variant), *textureKeys)


fun TextureMap(vararg entries: Pair<TextureKey, Identifier>, initializer: TextureMap.() -> Unit = {}): TextureMap {
    val textureMap = TextureMap()
    entries.forEach {
        textureMap.put(it.first, it.second)
    }
    initializer(textureMap)
    return textureMap
}

val TextureKey.string get() = this.toString()
