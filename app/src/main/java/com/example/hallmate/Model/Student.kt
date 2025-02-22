package com.example.hallmate.Model

import com.google.firebase.database.PropertyName

data class Student(
    var hallId:String?=null,
    var studentId:String?=null,
    var name:String?=null,
    var email:String?=null,
    var phone:String?=null,
    var department:String?=null,
    var batch:String?=null,
    var roomNo:String?=null,
    @get:PropertyName("isCommitteeMember")
    var isCommitteeMember:Boolean?=false,
    var dueAmount:Double?=null,
    var key:String?=null,
    var profilePic:String?=null,
    var password:String?=null,
    var mealCode:String?=null,
    @get:PropertyName("isStart")
    var isStart:Boolean?=null,
    @get:PropertyName("isMutton")
    var isMutton:Boolean?=null,
    )
