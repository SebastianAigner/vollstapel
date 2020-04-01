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


interface AppState : RState {
    var cartItems: List<CartItem>
}

class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        cartItems = listOf()
        GlobalScope.launch {
            refreshCart()
        }
    }

    suspend fun refreshCart() {
        val newItems = obtainCart()
        setState {
            cartItems = newItems
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
                            refreshCart()
                        }
                    }
                }
            }
        }
        child(InputComponent::class) {
            key = "inComponent"
            attrs.onSubmit = { input ->
                val cartItem = CartItem(input.replace("!", ""), input.count { it == '!' })
                GlobalScope.launch {
                    sendCartItem(cartItem)
                    refreshCart()
                }
            }
        }
    }
}

