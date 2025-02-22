package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class MealForRV(
    var month:String?=null,
    var date:String?=null,
    var hallId:String?=null,

    var bstatus:Boolean?=null,
    var dstatus:Boolean?=null,
    var lstatus:Boolean?=null,

    @get:PropertyName("isSahari")
    var isSahari:Boolean?=null,

    @get:PropertyName("isRamadan")
    var isRamadan:Boolean?=null,

    var bstatusDay:Boolean?=null,
    var dstatusDay:Boolean?=null,
    var lstatusDay:Boolean?=null,

    var bIsAutoMeal:Boolean?=null,
    var lIsAutoMeal:Boolean?=null,
    var dIsAutoMeal:Boolean?=null,


    var isUpdate:Boolean?=null,
    var isLessThanTwo:Boolean?=null,


    )
