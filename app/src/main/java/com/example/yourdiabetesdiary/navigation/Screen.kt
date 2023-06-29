package com.example.yourdiabetesdiary.navigation

sealed class Screen(val route: String) {
    object Home : Screen(HOME_ROUTE)
    object Authentication : Screen(AUTHENTICATION_ROUTE)
    object DiaryEntry : Screen(WRITE_ROUTE) {
        fun passArgs(diaryId: String) = WRITE_ROUTE.replace("{$DIARY_ID_ARGUMENT_KEY}", diaryId)
        const val DIARY_ID_ARGUMENT_KEY = "diaryId"
    }

    companion object {
        private const val HOME_ROUTE = "home_screen"
        private const val AUTHENTICATION_ROUTE = "authentication_screen"
        private const val WRITE_ROUTE =
            "diary_entry_screen?${DiaryEntry.DIARY_ID_ARGUMENT_KEY}={${DiaryEntry.DIARY_ID_ARGUMENT_KEY}}"
    }
}