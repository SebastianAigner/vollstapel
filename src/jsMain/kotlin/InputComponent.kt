import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.form
import react.dom.input

interface InputState : RState {
    var text: String
}

interface InputProps : RProps {
    var onSubmit: (String) -> Unit
}

class InputComponent : RComponent<InputProps, InputState>() {
    override fun InputState.init() {
        text = ""
    }

    val submit: (Event) -> Unit = {
        it.preventDefault()
        setState { text = "" } // setState runs asynchronously!
        props.onSubmit(state.text)
    }

    val change: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        setState { text = value }
    }

    override fun RBuilder.render() {
        form {
            attrs.onSubmitFunction = submit
            input(InputType.text) {
                attrs.onChangeFunction = change
                attrs.value = state.text
            }
        }
    }
}