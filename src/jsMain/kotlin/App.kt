import kotlinx.coroutines.GlobalScope
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

    document.body?.appendChild(list)
    document.body?.addForm()
    GlobalScope.launch {
        cartItems = obtainCart()
    }
}

fun HTMLElement.addForm() {
    fun handleSubmit(e: Event) {
        e.preventDefault()
        val element = document.getElementById("listInput") as HTMLInputElement
        val input = element.value
        element.value = ""
        val cartItem = CartItem(input.replace("!", ""), input.count { it == '!' })
        GlobalScope.launch {
            sendCartItem(cartItem)
            cartItems = obtainCart()
        }
    }

    append {
        form {
            onSubmitFunction = ::handleSubmit
            input {
                id = "listInput"
            }
        }
    }
}


fun HTMLUListElement.renderCart(cart: List<CartItem>) {
    list.textContent = ""
    cart.forEach { cartItem ->
        append.li {
            onClickFunction = {
                GlobalScope.launch {
                    deleteCartItem(cartItem)
                    cartItems = obtainCart()
                }
            }
            +cartItem.desc
        }
    }
}
