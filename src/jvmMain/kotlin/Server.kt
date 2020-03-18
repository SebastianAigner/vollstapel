import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

val todoItems = mutableListOf(
    CartItem("Cucumbers 🥒", 1),
    CartItem("Tomatoes 🍅", 2),
    CartItem("Orange Juice 🍊", 3)
)

fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            anyHost()
        }

        routing {
            static("/static") {
                resources("")
            }
            get(CartItem.path) {
                call.respond(todoItems)
            }
            post(CartItem.path) {
                val myDataClass = call.receive<CartItem>()
                println("received $myDataClass")
                todoItems += myDataClass
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}