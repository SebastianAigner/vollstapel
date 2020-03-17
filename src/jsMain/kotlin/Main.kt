import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.get
import io.ktor.utils.io.core.use
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.WebSocket
import kotlin.browser.document

import react.dom.*
import kotlin.browser.document

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}

//suspend fun main() {
//    println("Hello, Full Stack!")
//    val root = document.getElementById("root") as HTMLDivElement
//    val httpClient = HttpClient() {
//        install(JsonFeature) {
//            serializer = KotlinxSerializer()
//        }
//    }
//    val res = httpClient.get<TodoItem>("http://localhost:9090/todos")
//    root.innerHTML = "Example response: $res"
//}
