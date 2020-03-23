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

    val submitHandler: (Event) -> Unit = {
        it.preventDefault()
        setState { text = "" } // setState runs asynchronously!
        props.onSubmit(state.text)
    }

    val changeHandler: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        setState { text = value }
    }

    override fun RBuilder.render() {
        form {
            attrs.onSubmitFunction = submitHandler
            input(InputType.text) {
                attrs.onChangeFunction = changeHandler
                attrs.value = state.text
            }
        }
    }
}