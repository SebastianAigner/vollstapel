import kotlinx.serialization.Serializable

@Serializable
data class CartItem(val desc: String, val priority: Int) {
    companion object {
        val path = "/cart"
    }
}