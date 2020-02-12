import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.dom.*
import react.*

interface AppState: RState {
    var platform: String?
    var messageLog: String
}

class App: RComponent<RProps, AppState>() {
    override fun AppState.init() {
        messageLog = ""
        val wsClient = HttpClient() {
            install(WebSockets)
        }
        GlobalScope.launch {
            wsClient.ws(
                method = HttpMethod.Get,
                host = "127.0.0.1",
                port = 9090
            ) {
                send("HELLO WORLD")
                while(true) {
                    val frame = incoming.receive()
                    if (frame is Frame.Text) setState {
                        messageLog + frame.readText() + "\n"
                    }
                }
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