import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.browser.window

val endpoint = with(window.location) {
    listOf(
        if (protocol.contains("s")) "https" else "http",
        "://",
        host
    ).joinToString("")
} // makeshift fix until https://github.com/ktorio/ktor/issues/1695 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun obtainCart(): List<CartItem> {
    return jsonClient.get(endpoint + CartItem.path)
}

suspend fun sendCartItem(cartItem: CartItem) {
    jsonClient.post<Unit>(endpoint + CartItem.path) {
        contentType(ContentType.Application.Json)
        body = cartItem
    }
}

suspend fun deleteCartItem(cartItem: CartItem) {
    jsonClient.delete<Unit>(endpoint + CartItem.path + "/${cartItem.id}")
}