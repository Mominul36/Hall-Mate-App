package com.example.hallmate.Model

data class DayMealStatusForRecycler(
    var date:String?=null,
    var month:String?=null,
    var isRamadan:Boolean?=null,

    var bstatus:Boolean?=null,
    var bisMuttonOrBeaf:Boolean?=null,
    var bisAutoMeal:Boolean?=null,
    var bmealCost:Double?=null,
    var bfuelCost:Double?=null,
    var bextraMuttonCost:Double?=null,

    var lstatus:Boolean?=null,
    var lisMuttonOrBeaf:Boolean?=null,
    var lisAutoMeal:Boolean?=null,
    var lmealCost:Double?=null,
    var lfuelCost:Double?=null,
    var lextraMuttonCost:Double?=null,

    var dstatus:Boolean?=null,
    var disMuttonOrBeaf:Boolean?=null,
    var disAutoMeal:Boolean?=null,
    var dmealCost:Double?=null,
    var dfuelCost:Double?=null,
    var dextraMuttonCost:Double?=null,

)
