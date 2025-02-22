package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class Meal(
    var month:String?=null,
    var date:String?=null,
    var hallId:String?=null,
    var period:String?=null,
    var status:Boolean?=null,

    @get:PropertyName("isMutton")
    var isMutton:Boolean?=null,
    @get:PropertyName("isComplete")
    var isComplete:Boolean?=null,
    @get:PropertyName("isSahari")
    var isSahari:Boolean?=null,
    )
