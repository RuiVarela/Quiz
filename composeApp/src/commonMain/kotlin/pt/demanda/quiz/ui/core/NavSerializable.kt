package pt.demanda.quiz.ui.core

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import net.thauvin.erik.urlencoder.UrlEncoderUtil

inline fun <reified T : Any> navSerializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {

    override fun get(bundle: Bundle, key: String): T? {
        val v = bundle.getString(key)?.let(::decode)
        return v
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, encode(value))
    }


    override fun parseValue(value: String): T = decode(value)
    override fun serializeAsValue(value: T): String = encode(value)

    //
    // actual encoding
    //
    private fun decode(value: String): T {
        val decoded = UrlEncoderUtil.decode(value)
        val value = json.decodeFromString<T>(decoded)
        return value
    }

    private fun encode(value: T): String {
        val json = json.encodeToString(value)
        val encoded = UrlEncoderUtil.encode(json)
        return encoded
    }
}