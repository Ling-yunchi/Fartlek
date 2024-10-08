package com.lingyunchi.fartlek.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import java.math.BigDecimal

@SuppressWarnings("UNCHECKED_CAST")
@Composable
fun <T : Number> OutlinedNumberField(
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var numberValue by remember { mutableStateOf(value) }
    var textValue by remember { mutableStateOf(value.toString()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = textValue,
        onValueChange = {
            textValue = it
        },
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) {
                try {
                    val convertedValue: T? = when (value) {
                        is Int -> textValue.toIntOrNull() as? T
                        is Long -> textValue.toLongOrNull() as? T
                        is Float -> textValue.toFloatOrNull() as? T
                        is Double -> textValue.toDoubleOrNull() as? T
                        is Short -> textValue.toShortOrNull() as? T
                        is Byte -> textValue.toByteOrNull() as? T
                        is BigDecimal -> textValue.toBigDecimalOrNull() as? T
                        else -> throw IllegalArgumentException("Unsupported number type")
                    }

                    // 如果转换成功，调用 onValueChange 进行更新
                    if (convertedValue != null) {
                        numberValue = convertedValue
                        textValue = numberValue.toString()
                        onValueChange(convertedValue)
                    } else {
                        textValue = numberValue.toString() // 如果转换失败，恢复为之前的值
                    }
                } catch (e: Exception) {
                    textValue = numberValue.toString()
                }
            }
        },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
        }),
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}