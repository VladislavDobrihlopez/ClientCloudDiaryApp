package com.example.yourdiabetesdiary.domain.exceptions

sealed class CustomException(errorText: String) : Exception(errorText) {
    class UserNotAuthenticatedException() :
        CustomException("Is seems token has been expired. User should authenticate to the app again")
}
