package com.example.myauto.data

enum class SortType { DATE, COST, VOLUME, CATEGORY }
enum class SortOrder { DESC, ASC }

fun SortOrder.reverse() = when (this) {
    SortOrder.DESC -> SortOrder.ASC
    SortOrder.ASC -> SortOrder.DESC
}