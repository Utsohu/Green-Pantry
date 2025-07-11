package com.example.greenpantry.ui.sharedcomponents

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greenpantry.ui.theme.green_primary



@Composable
fun TextEntryModule(
    description:String,
    hint:String,
    textValue:String,
    keyboardType: KeyboardType = KeyboardType.Ascii,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textColor: Color,
    cursorColor: Color,
    onValueChanged:(String) -> Unit,
    trailingIcon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
    onTrailingIconClick:(() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
    ){
        Text(
            text = description,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp)
                .border(0.5.dp, textColor, RoundedCornerShape(25.dp))
                .height(50.dp)
                .shadow(3.dp, RoundedCornerShape(25.dp)),
            value = textValue,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                cursorColor = cursorColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = onValueChanged,
            shape = RoundedCornerShape(25.dp),
            singleLine = true,
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = cursorColor
                    )
                }
            },
            trailingIcon = {
                if(trailingIcon != null){
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = cursorColor,
                        modifier = Modifier
                            .clickable {
                                if(onTrailingIconClick != null) onTrailingIconClick()
                            }
                    )
                }
            },
            placeholder = {
                Text(
                    hint,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextEntryModulePreview(){
    TextEntryModule(
        description = "Email address",
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp,0.dp,10.dp,5.dp),
        hint = "Enter valid email",
        leadingIcon = Icons.Default.Email,
        textValue = "TextInput",
        textColor = Color.Black,
        cursorColor = green_primary,
        onValueChanged = {},
        //trailingIcon = Icons.Filled.RemoveRedEye,
        onTrailingIconClick = {},
        visualTransformation = PasswordVisualTransformation()
    )
}