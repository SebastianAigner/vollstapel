import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(val desc: String, val priority: Int)