package com.example.hallmate.Model

data class StudentHallBill(
    var date:String?=null,
    var bstatus:Boolean?=null,
    var lstatus:Boolean?=null,
    var dstatus:Boolean?=null,

    var bisShowing:Boolean?=null,
    var lisShowing:Boolean?=null,
    var disShowing:Boolean?=null,

    var bCost:Double?=null,
    var lCost:Double?=null,
    var dCost:Double?=null,
    var totalCost:Double?=null,

    )
