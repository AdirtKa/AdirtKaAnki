package com.example.adirtkaanki.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adirtkaanki.ui.components.AppTextField
import com.example.adirtkaanki.ui.components.LoadingButton


@Composable
fun LoginForm(
    username: String,
    password: String,
    errorMessage: String?,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRegisterClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val formElementModifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(0.8f)

        Text(
            text = "Добро пожаловать в Adki",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = formElementModifier,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(6.dp))

        AppTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = "Введите логин",
            modifier = formElementModifier,
        )

        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Введите пароль",
            modifier = formElementModifier,
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }

        LoadingButton(
            text = "Войти",
            isLoading = isLoading,
            onClick = onSubmit,
            modifier = formElementModifier
        )

        TextButton(onClick = onRegisterClick) {
            Text("Еще нет аккаунта? Зарегистрироваться")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    LoginForm(
        username = "daniil",
        password = "",
        errorMessage = null,
        isLoading = true,
        onUsernameChange = {},
        onPasswordChange = {},
        onSubmit = {},
        onRegisterClick = {}
    )
}