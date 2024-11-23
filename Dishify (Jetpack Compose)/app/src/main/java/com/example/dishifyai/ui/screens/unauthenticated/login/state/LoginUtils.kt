package com.example.dishifyai.ui.screens.unauthenticated.login.state

import com.example.dishifyai.ui.common.state.ErrorState
import com.example.dishifyai.R


val emailOrMobileEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_email_mobile
)

val passwordEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_password
)