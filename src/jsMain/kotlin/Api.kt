import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.browser.window

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

@Serializable
data class ShoppingListItem(val desc: String, val priority: Int)

fun main() {
    val item = ShoppingListItem("Hello", 3)
    GlobalScope.launch {
        jsonClient.post<Unit>(endpoint) {
            contentType(ContentType.Application.Json)
            body = item
        }
    }
}