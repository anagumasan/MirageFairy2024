package miragefairy2024.util

import net.minecraft.text.Text

inline fun text(block: TextScope.() -> Text) = block(TextScope)

object TextScope {
    operator fun String.invoke(): Text = Text.of(this)
    fun translate(key: String): Text = Text.translatable(key)
    fun translate(key: String, vararg args: Any?): Text = Text.translatable(key, *args)
    operator fun Text.plus(text: Text): Text = Text.empty().append(this).append(text)
}
