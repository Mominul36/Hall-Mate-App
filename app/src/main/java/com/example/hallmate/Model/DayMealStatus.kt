package com.example.hallmate.Model

data class DayMealStatus(
    var date:String?=null,
    var period:String?=null,
    var status:Boolean?=null,
    var isMuttonOrBeaf:Boolean?=null,
    var isAutoMeal:Boolean?=null,
    var mealCost:Double?=null,
    var fuelCost:Double?=null,
    var extraMuttonCost:Double?=null,
    var guestMeal:Int?=null,
)
