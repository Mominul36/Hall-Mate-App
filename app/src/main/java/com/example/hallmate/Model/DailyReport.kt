package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class DailyReport(
    //@get:PropertyName("month")
    var month:String?=null,
    //@get:PropertyName("date")
    var date:String?=null,
    @get:PropertyName("isRamadan")
    var isRamadan:Boolean?=null,

   // @get:PropertyName("bStudentMeal")
    var bStudentMeal:String?=null,
   // @get:PropertyName("bGuestMeal")
    var bGuestMeal:String?=null,
    //@get:PropertyName("bMuttonMeal")
    var bMuttonMeal:String?=null,


   // @get:PropertyName("lStudentMeal")
    var lStudentMeal:String?=null,
    //@get:PropertyName("lGuestMeal")
    var lGuestMeal:String?=null,
   // @get:PropertyName("lMuttonMeal")
    var lMuttonMeal:String?=null,
    //@get:PropertyName("SahariMeal")
    var SahariMeal:String?=null,


    //@get:PropertyName("dStudentMeal")
    var dStudentMeal:String?=null,
    //@get:PropertyName("dGuestMeal")
    var dGuestMeal:String?=null,
   // @get:PropertyName("dMuttonMeal")
    var dMuttonMeal:String?=null,

    )
