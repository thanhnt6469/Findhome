package com.app.findhome.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.app.findhome.data.model.ValidationResult
import com.app.findhome.ui.theme.Black
import com.app.findhome.ui.theme.focusedTextFieldText
import com.app.findhome.ui.theme.textFieldContainer
import com.app.findhome.ui.theme.unfocusedTextFieldText

@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailing: String,
    validationResult: ValidationResult?,
    isPassword: Boolean = false
) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    val isValid = validationResult?.status ?: true
    val focusedIndicatorColor = if (isValid) Color.Blue else Color.Red
    val unfocusedIndicatorColor = if (isValid) Color.Gray else Color.Red

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = uiColor, fontWeight = FontWeight.Normal)
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.unfocusedTextFieldText,
            focusedPlaceholderColor = MaterialTheme.colorScheme.focusedTextFieldText,
            unfocusedContainerColor = MaterialTheme.colorScheme.textFieldContainer,
            focusedContainerColor = MaterialTheme.colorScheme.textFieldContainer,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor
        ),
        trailingIcon = {
            if(trailing.isNotEmpty()) {
                TextButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = trailing,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = uiColor
                    )
                }
            } else {
                Text("")
            }
        }
    )
}
