import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.file
import io.ktor.http.content.files
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

object Humans {
    val text = javaClass.classLoader.getResource("humans.txt").readText()
}

val todoItems = listOf(
    TodoItem("Buy cat", 1),
    TodoItem("Eat laundry", 2),
    TodoItem("Milk pizza", 3)
)

fun main() {
    println("Made by ${Humans.text}")
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            serialization()
        }
        install(CORS) {
            method(HttpMethod.Get)
            anyHost()
        }

        routing {
            static("/static") {
                resources("")
            }
            get("/todos") {
                call.respond(todoItems.first())
            }
//            webSocket("/") {
//                for (frame in incoming) {
//                    when (frame) {
//                        is Frame.Text -> {
//                            val text = frame.readText()
//                            outgoing.send(Frame.Text("YOU SAID: $text"))
//                            if (text.equals("bye", ignoreCase = true)) {
//                                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
//                            }
//                        }
//                    }
//                }
//            }
        }
    }.start(wait = true)
}