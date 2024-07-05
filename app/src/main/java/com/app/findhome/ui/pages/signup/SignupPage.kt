package com.app.findhome.ui.pages.signup

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.findhome.R
import com.app.findhome.data.model.ValidationResult
import com.app.findhome.ui.components.LoadingAnimation
import com.app.findhome.ui.components.LoginTextField
import com.app.findhome.ui.components.SocialMediaLogIn
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.login.LoginState
import com.app.findhome.ui.pages.login.LoginViewModel
import com.app.findhome.ui.theme.Black
import com.app.findhome.ui.theme.BlueGray
import com.app.findhome.ui.theme.Roboto
import com.shashank.sony.fancytoastlib.FancyToast

@Composable
@Preview
fun Prev(){
    SignupPage(rememberNavController() , state = LoginState(), {},{})
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SignupPage(navController: NavController, state: LoginState, onLoginClick: () -> Unit, onGoogleSignup: (String) -> Unit) {
    val viewModel: SignupViewModel = viewModel()
    val viewModel1: LoginViewModel = viewModel()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()

    val firstNameError by viewModel.firstNameError.collectAsState()
    val lastNameError by viewModel.lastNameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    val signupSuccess by viewModel.signupSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            FancyToast.makeText(
                context,
                "Signup successful",
                FancyToast.LENGTH_LONG,
                FancyToast.SUCCESS,
                true
            ).show()
            navController.navigate(AppPage.HomePage.route)
            viewModel.resetState()
        }
    }

    LaunchedEffect(key1 = state.loginError) {
        state.loginError?.let { error ->
            FancyToast.makeText(context, error, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show()
        }
    }

    LaunchedEffect(viewModel.logout.value) {
        if (viewModel.logout.value == true) {
            navController.navigate(AppPage.LoginPage.route)
            viewModel.resetLogout()
        }
    }

    LaunchedEffect(signupSuccess) {
        if (signupSuccess) {
            FancyToast.makeText(
                context,
                "Signup successful",
                FancyToast.LENGTH_LONG,
                FancyToast.SUCCESS,
                true
            ).show()
            viewModel.resetSignupSuccess()
            navController.navigate(AppPage.HomePage.route)
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            FancyToast.makeText(context, it, FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show()
            viewModel.resetLogMessage()
        }

//        if (errorMessage != null) {
//            FancyToast.makeText(context,errorMessage,FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show()
//            viewModel.resetLogMessage()
//        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                TopSection()
                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    SignupSection(
                        firstName = firstName,
                        onFirstNameChange = viewModel::onFirstNameChange,
                        firstNameError = firstNameError,
                        lastName = lastName,
                        onLastNameChange = viewModel::onLastNameChange,
                        lastNameError = lastNameError,
                        email = email,
                        onEmailChange = viewModel::onEmailChange,
                        emailError = emailError,
                        password = password,
                        onPasswordChange = viewModel::onPasswordChange,
                        passwordError = passwordError,
                        selectedRole = selectedRole,
                        onRoleChange = viewModel::onRoleChange,
                        onSignupClick = {
                            //viewModel.validateFields()
                            viewModel.createUserFireBase(email, password, selectedRole)
                        },
                        isFormValid = isFormValid
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SocialMediaSection(onGoogleSignup = { onGoogleSignup(selectedRole) })
                    Spacer(modifier = Modifier.height(20.dp))
                    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(fraction = 0.8f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                        fontFamily = Roboto,
                                        fontWeight = FontWeight.Normal
                                    )
                                ) {
                                    append("Ban đã có tài khoản?")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = uiColor,
                                        fontSize = 14.sp,
                                        fontFamily = Roboto,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append(" ")
                                    append("Đăng nhập")
                                }
                            },
                            modifier = Modifier.clickable {
                                onLoginClick()
                            }
                        )

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        if (viewModel.signupProgress.value || viewModel1.loginProgress.value) {
            LoadingAnimation()
        }
    }
}

@Composable
private fun SocialMediaSection(onGoogleSignup: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "hoặc",
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFF64748B))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialMediaLogIn(
                icon = R.drawable.google,
                text = "Google",
                modifier = Modifier.weight(1f),
                onClick = onGoogleSignup
            )
        }
    }
}

@Composable
private fun SignupSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    firstNameError: ValidationResult?,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    lastNameError: ValidationResult?,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: ValidationResult?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: ValidationResult?,
    selectedRole: String,
    onRoleChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    isFormValid: Boolean
) {
    LoginTextField(
        label = "Tên",
        value = firstName,
        onValueChange = onFirstNameChange,
        trailing = "",
        modifier = Modifier.fillMaxWidth(),
        validationResult = firstNameError
    )
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        label = "Họ",
        value = lastName,
        onValueChange = onLastNameChange,
        trailing = "",
        modifier = Modifier.fillMaxWidth(),
        validationResult = lastNameError
    )
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        label = "Email",
        value = email,
        onValueChange = onEmailChange,
        trailing = "",
        modifier = Modifier.fillMaxWidth(),
        validationResult = emailError
    )
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        label = "Mật khẩu",
        value = password,
        onValueChange = onPasswordChange,
        trailing = "",
        modifier = Modifier.fillMaxWidth(),
        validationResult = passwordError
    )
    Spacer(modifier = Modifier.height(15.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(selected = selectedRole == "Khách", onClick = { onRoleChange("Khách") })
        Text(text = "Khách", modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Normal)
        Spacer(modifier = Modifier.width(16.dp))
        RadioButton(selected = selectedRole == "Chủ", onClick = { onRoleChange("Chủ") })
        Text(text = "Chủ", modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Normal)
    }
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onClick = onSignupClick,
        enabled = isFormValid && selectedRole.isNotEmpty(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        Text(
            text = "Đăng ký",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@SuppressLint("ResourceType", "InvalidColorHexValue")
@Composable
private fun TopSection() {
    //val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.5f),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Column(
            Modifier.padding(top = 60.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "",
                    tint= Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.findhome),
                    contentDescription = "",
                    tint= Color.Unspecified
                )
            }
        }
        CenterSection()
    }
}

@SuppressLint("InvalidColorHexValue")
@Composable
private fun CenterSection() {
    Column(Modifier.padding(top = 310.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(parseColor("#13497B")),
                            fontSize = 40.sp,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Welcome")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF4D000000),
                            fontSize = 20.sp,
                            fontFamily = Roboto,
                        )
                    ) {
                        append("\nSignup for enjoy findhome")
                    }
                }, textAlign = TextAlign.Center
            )
        }
    }
}