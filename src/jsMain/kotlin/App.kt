import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.*
import react.dom.h1
import react.dom.p

val endpoint = "http://localhost:9090"

val jsonClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

interface AppState : RState {
    var cartItems: List<CartItem>
}

class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        cartItems = listOf()
        GlobalScope.launch { obtainTodos() }
    }

    suspend fun obtainTodos() {
        val result = jsonClient.get<List<CartItem>>(endpoint + CartItem.path)
        setState { cartItems = result }
    }

    suspend fun sendCartItem(cartItem: CartItem) {
        jsonClient.post<Unit>(endpoint + CartItem.path) {
            contentType(ContentType.Application.Json)
            body = cartItem
        }
    }

    val addTodo: (String) -> Unit = { input ->
        val cartItem = CartItem(input.replace("!", ""), input.count { it == '!' })
        setState { cartItems += cartItem }
        GlobalScope.launch { sendCartItem(cartItem) }
    }

    override fun RBuilder.render() {
        h1 {
            +"Full-Stack Shopping List"
        }
        state.cartItems.sortedByDescending(CartItem::priority).forEach {
            p {
                key = it.toString()
                +"[${it.priority}] ${it.desc}"
            }
        }
        child(InputComponent::class) {
            key = "inComponent"
            attrs.onSubmit = addTodo
        }
    }
}

