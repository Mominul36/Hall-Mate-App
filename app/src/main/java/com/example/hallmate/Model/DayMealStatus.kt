package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class DayMealStatus(
    var date:String?=null,
    var period:String?=null,
    var status:Boolean?=null,
    @get:PropertyName("isMuttonOrBeaf")
    var isMuttonOrBeaf:Boolean?=null,
    @get:PropertyName("isAutoMeal")
    var isAutoMeal:Boolean?=null,
    var mealCost:Double?=null,
    var fuelCost:Double?=null,
    var extraMuttonCost:Double?=null,
    var guestMeal:Int?=null,
)
