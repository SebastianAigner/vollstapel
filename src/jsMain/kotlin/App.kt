import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.dom.*
import react.*

interface AppState: RState {
    var platform: String?
    var messageLog: List<String>
    var todos: List<TodoItem>
}

class App: RComponent<RProps, AppState>() {
    override fun AppState.init() {
        messageLog = emptyList()
        todos = listOf()
        val jsonClient = HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
        GlobalScope.launch {
            val result = jsonClient.get<List<TodoItem>>("http://localhost:9090/todos")
            setState {
                todos = result
            }
        }
    }

    override fun RBuilder.render() {
        h1 {
            +"Hello, ${state.platform ?: ""}!"
        }
        child(Button::class) {
            attrs {
                onClick = {setState { platform = "other" }}
                label = "other"
            }
        }
        child(Button::class) {
            attrs {
                onClick = {setState { platform = "this" }}
                label = "this"
            }
        }
        state.messageLog.forEach {
            p {
                +it
            }
        }
        state.todos.forEach {
            p {
                +it.toString()
            }
        }
    }
}

interface ButtonProps: RProps {
    var label: String
    var onClick: () -> Unit
}

class Button: RComponent<ButtonProps, RState>() {
    override fun RBuilder.render() {
        button {
            attrs {
                onClickFunction = { props.onClick() }
            }
            +props.label
        }
    }
}