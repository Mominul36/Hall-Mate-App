package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class Meal(
    @get:PropertyName("month")
    var month:String?=null,
    @get:PropertyName("date")
    var date:String?=null,
    @get:PropertyName("hallId")
    var hallId:String?=null,
    @get:PropertyName("isSahari")
    var isSahari:Boolean?=null,

    @get:PropertyName("bstatus")
    var bstatus:Boolean?=null,
    @get:PropertyName("bisMutton")
    var bisMutton:Boolean?=null,
    @get:PropertyName("bisComplete")
    var bisComplete:Boolean?=null,

    @get:PropertyName("lstatus")
    var lstatus:Boolean?=null,
    @get:PropertyName("lisMutton")
    var lisMutton:Boolean?=null,
    @get:PropertyName("lisComplete")
    var lisComplete:Boolean?=null,

    @get:PropertyName("dstatus")
    var dstatus:Boolean?=null,
    @get:PropertyName("disMutton")
    var disMutton:Boolean?=null,
    @get:PropertyName("disComplete")
    var disComplete:Boolean?=null,

    )
