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
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.litote.kmongo.async.KMongo
import org.litote.kmongo.async.getCollection
import org.litote.kmongo.coroutine.deleteOne
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq

//
//val todoItems = mutableListOf(
//    CartItem("Cucumbers 🥒", 1),
//    CartItem("Tomatoes 🍅", 2),
//    CartItem("Orange Juice 🍊", 3)
//)

val client = KMongo.createClient()
val database = client.getDatabase("test")
val collection = database.getCollection<CartItem>()

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
                call.respond(collection.find().toList())
            }
            post(CartItem.path) {
                collection.insertOne(call.receive<CartItem>())
                call.respond(HttpStatusCode.OK)
            }
            delete(CartItem.path) {
                val received = call.receive<CartItem>()
                collection.deleteOne(CartItem::desc eq received.desc)
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}