package com.example.abhiyant.navigation

sealed class Screen(val route: String) {
    object InspectionList : Screen("inspection_list")
    object InspectionEntry : Screen("inspection_entry") {
        const val ROUTE_WITH_ID = "inspection_entry/{inspectionId}"
        fun createRoute(inspectionId: Long? = null) = if (inspectionId != null && inspectionId > 0) "inspection_entry/$inspectionId" else "inspection_entry"
    }
    data class InspectionDetail(val inspectionId: Long = 0L) : Screen("inspection_detail/{inspectionId}") {
        fun createRoute(inspectionId: Long) = "inspection_detail/$inspectionId"
    }
}

