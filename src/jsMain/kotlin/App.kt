import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.h1
import react.dom.li
import react.dom.ul

val App = functionalComponent<RProps> { _ ->
    val (cart, setCart) = useState(emptyList<CartItem>())

    useEffect(dependencies = listOf()) {
        GlobalScope.launch {
            val newValue = obtainCart()
            setCart(newValue)
        }
    }

    h1 {
        +"Full-Stack Shopping List"
    }
    ul {
        cart.sortedByDescending(CartItem::priority).forEach { item ->
            li {
                key = item.toString()
                +"[${item.priority}] ${item.desc} "
                attrs.onClickFunction = {
                    GlobalScope.launch {
                        deleteCartItem(item)
                        setCart(obtainCart())
                    }
                }
            }
        }
    }

    child(
        functionalComponent = InputComponent,
        props = jsObject {
            onSubmit = { input ->
                val cartItem = CartItem(input.replace("!", ""), input.count { it == '!' })
                GlobalScope.launch {
                    sendCartItem(cartItem)
                    setCart(obtainCart())
                }
            }
        }
    )
}

