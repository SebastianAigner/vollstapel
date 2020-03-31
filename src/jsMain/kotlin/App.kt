import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.h1
import react.dom.p
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

interface AppState : RState {
    var cartItems: List<CartItem>
}

class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        cartItems = listOf()
        GlobalScope.launch { obtainCart() }
    }

    suspend fun obtainCart() {
        val result = jsonClient.get<List<CartItem>>(endpoint + CartItem.path)
        setState { cartItems = result }
    }

    suspend fun sendCartItem(cartItem: CartItem) {
        jsonClient.post<Unit>(endpoint + CartItem.path) {
            contentType(ContentType.Application.Json)
            body = cartItem
        }
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        jsonClient.delete<Unit>(endpoint + CartItem.path) {
            contentType(ContentType.Application.Json)
            body = cartItem
        }
    }

    val addCartItem: (String) -> Unit = { input ->
        val cartItem = CartItem(input.replace("!", ""), input.count { it == '!' })
        //setState { cartItems += cartItem }
        GlobalScope.launch {
            sendCartItem(cartItem)
            obtainCart()
        }
    }

    override fun RBuilder.render() {
        h1 {
            +"Full-Stack Shopping List"
        }
        state.cartItems.sortedByDescending(CartItem::priority).forEach {
            p {
                key = it.toString()
                +"[${it.priority}] ${it.desc} "
                button {
                    attrs.onClickFunction = { e ->
                        GlobalScope.launch {
                            deleteCartItem(it)
                            obtainCart()
                        }
                    }
                }
            }
        }
        child(InputComponent::class) {
            key = "inComponent"
            attrs.onSubmit = addCartItem
        }
    }
}

