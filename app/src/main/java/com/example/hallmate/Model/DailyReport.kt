package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class DailyReport(
    @get:PropertyName("month")
    var month:String?=null,
    @get:PropertyName("date")
    var date:String?=null,
    @get:PropertyName("isRamadan")
    var isRamadan:Boolean?=null,

    @get:PropertyName("bStudentMeal")
    var bStudentMeal:Int?=null,
    @get:PropertyName("bGuestMeal")
    var bGuestMeal:Int?=null,
    @get:PropertyName("bMuttonMeal")
    var bMuttonMeal:Int?=null,


    @get:PropertyName("lStudentMeal")
    var lStudentMeal:Int?=null,
    @get:PropertyName("lGuestMeal")
    var lGuestMeal:Int?=null,
    @get:PropertyName("lMuttonMeal")
    var lMuttonMeal:Int?=null,
    @get:PropertyName("SahariMeal")
    var SahariMeal:Int?=null,


    @get:PropertyName("dStudentMeal")
    var dStudentMeal:Int?=null,
    @get:PropertyName("dGuestMeal")
    var dGuestMeal:Int?=null,
    @get:PropertyName("dMuttonMeal")
    var dMuttonMeal:Int?=null,

    )
