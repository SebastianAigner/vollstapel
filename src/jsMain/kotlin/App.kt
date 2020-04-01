import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.h1
import react.dom.li
import react.dom.ul


interface AppState : RState {
    var cartItems: List<CartItem>
}

class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        cartItems = listOf()
        GlobalScope.launch {
            refreshCart()
        }
    }

    suspend fun refreshCart() {
        val newItems = obtainCart()
        setState {
            cartItems = newItems
        }
    }

    override fun RBuilder.render() {
        h1 {
            +"Full-Stack Shopping List"
        }
        ul {
            state.cartItems.sortedByDescending(CartItem::priority).forEach { item ->
                li {
                    key = item.toString()
                    +"[${item.priority}] ${item.desc} "
                    attrs.onClickFunction = {
                        GlobalScope.launch {
                            deleteCartItem(item)
                            refreshCart()
                        }
                    }
                }
            }
        }
        child(InputComponent::class) {
            key = "inComponent"
            attrs.onSubmit = { input ->
                val cartItem = CartItem(input.replace("!", ""), input.count { it == '!' })
                GlobalScope.launch {
                    sendCartItem(cartItem)
                    refreshCart()
                }
            }
        }
    }
}

