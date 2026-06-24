package io.funstop.navigation

sealed class UiEvent  {

    object NavigationToList:UiEvent()

    data class NavigationToDetail(val productId: Int):UiEvent()

}