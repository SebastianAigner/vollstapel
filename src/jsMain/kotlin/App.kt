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

    fun addTodo(input: String) {
        val todoItem = CartItem(input, input.count { it == '!' })
        println("adding $todoItem")
        setState { cartItems += todoItem }
        GlobalScope.launch {
            jsonClient.post<Unit>(endpoint + CartItem.path) {
                contentType(ContentType.Application.Json)
                body = todoItem
            }
        }
    }

    override fun RBuilder.render() {
        h1 {
            +"News from JS!"
        }
        state.cartItems.forEach {
            p {
                key = it.toString()
                +it.toString()
            }
        }
        child(InputComponent::class) {
            key = "inComponent"
            attrs.onSubmit = { addTodo(it) }
        }
    }
}

