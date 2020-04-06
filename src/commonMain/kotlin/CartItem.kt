import kotlinx.serialization.Serializable

@Serializable
data class CartItem(val desc: String, val priority: Int) {
    val id: Int = desc.hashCode()
    companion object {
        const val path = "/cart"
    }
}