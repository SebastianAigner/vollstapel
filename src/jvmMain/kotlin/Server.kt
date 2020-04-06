import com.mongodb.ConnectionString
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
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


//val todoItems = mutableListOf(
//    CartItem("Cucumbers 🥒", 1),
//    CartItem("Tomatoes 🍅", 2),
//    CartItem("Orange Juice 🍊", 3)
//)

val connectionString: ConnectionString? = System.getenv("MONGODB_URI")?.let {
    ConnectionString("$it?retryWrites=false")
}

val client = if (connectionString != null) KMongo.createClient(connectionString) else KMongo.createClient()
val database = client.getDatabase(connectionString?.database ?: "test")
val collection = database.getCollection<CartItem>()

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090
    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }

        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
            get(CartItem.path) {
                call.respond(collection.find().toList())
            }
            post(CartItem.path) {
                collection.insertOne(call.receive<CartItem>())
                call.respond(HttpStatusCode.OK)
            }
            delete(CartItem.path + "/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                collection.deleteOne(CartItem::id eq id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}