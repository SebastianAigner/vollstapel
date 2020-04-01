import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.properties.Delegates

var cartItems: List<CartItem> by Delegates.observable(emptyList()) { _, _, new ->
    list.renderCart(new)
}

val list: HTMLUListElement = document.create.ul {} as HTMLUListElement

fun main() {
    document.body?.let { body ->
        body.addHeadline()
        body.appendChild(list)
        body.addForm()
    }

    GlobalScope.launch(Dispatchers.Default) {
        cartItems = obtainCart()
    }
}

fun HTMLElement.addHeadline() {
    append {
        h1 { +"Kotlin/Fullstack Shopping List"}
    }
}

fun HTMLElement.addForm() {
    fun handleSubmit(e: Event) {
        e.preventDefault()
        val input = document.getElementById("listInput") as HTMLInputElement
        val cartItem = CartItem(
            input.value.replace("!", ""),
            input.value.count { it == '!' }
        )
        input.value = ""
        GlobalScope.launch {
            sendCartItem(cartItem)
            cartItems = obtainCart()
        }
    }

    append {
        form {
            onSubmitFunction = ::handleSubmit
            input(InputType.text) {
                id = "listInput"
            }
        }
    }
}


fun HTMLUListElement.renderCart(cart: List<CartItem>) {
    list.textContent = ""
    append {
        cart.forEach { cartItem ->
            li {
                onClickFunction = {
                    GlobalScope.launch {
                        deleteCartItem(cartItem)
                        cartItems = obtainCart()
                    }
                }
                +"[${cartItem.priority}] ${cartItem.desc}"
            }
        }
    }
}
