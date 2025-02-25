package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class DayMealStatus(
    @get:PropertyName("date")
    var date:String?=null,
    @get:PropertyName("month")
    var month:String?=null,
    @get:PropertyName("isRamadan")
    var isRamadan:Boolean?=null,

    @get:PropertyName("bstatus")
    var bstatus:Boolean?=null,
    @get:PropertyName("bisMuttonOrBeaf")
    var bisMuttonOrBeaf:Boolean?=null,
    @get:PropertyName("bisAutoMeal")
    var bisAutoMeal:Boolean?=null,
    @get:PropertyName("bmealCost")
    var bmealCost:Double?=null,
    @get:PropertyName("bfuelCost")
    var bfuelCost:Double?=null,
    @get:PropertyName("bextraMuttonCost")
    var bextraMuttonCost:Double?=null,
    @get:PropertyName("btotalMeal")
    var btotalMeal:Int?=null,
    @get:PropertyName("bguestMeal")
    var bguestMeal:Int?=null,

    var bMuttonMeal:Int?=null,
    var bguestMuttonMeal:Int?=null,

    @get:PropertyName("lstatus")
    var lstatus:Boolean?=null,
    @get:PropertyName("lisMuttonOrBeaf")
    var lisMuttonOrBeaf:Boolean?=null,
    @get:PropertyName("lisAutoMeal")
    var lisAutoMeal:Boolean?=null,
    @get:PropertyName("lmealCost")
    var lmealCost:Double?=null,
    @get:PropertyName("lfuelCost")
    var lfuelCost:Double?=null,
    @get:PropertyName("lextraMuttonCost")
    var lextraMuttonCost:Double?=null,
    @get:PropertyName("ltotalMeal")
    var ltotalMeal:Int?=null,
    @get:PropertyName("lguestMeal")
    var lguestMeal:Int?=null,

    var lMuttonMeal:Int?=null,
    var lguestMuttonMeal:Int?=null,





    @get:PropertyName("dstatus")
    var dstatus:Boolean?=null,
    @get:PropertyName("disMuttonOrBeaf")
    var disMuttonOrBeaf:Boolean?=null,
    @get:PropertyName("disAutoMeal")
    var disAutoMeal:Boolean?=null,
    @get:PropertyName("dmealCost")
    var dmealCost:Double?=null,
    @get:PropertyName("dfuelCost")
    var dfuelCost:Double?=null,
    @get:PropertyName("dextraMuttonCost")
    var dextraMuttonCost:Double?=null,
    @get:PropertyName("dtotalMeal")
    var dtotalMeal:Int?=null,
    @get:PropertyName("dguestMeal")
    var dguestMeal:Int?=null,

    var dMuttonMeal:Int?=null,
    var dguestMuttonMeal:Int?=null
)
