package com.example.hallmate.Model

data class Student(
    var hallId:String?=null,
    var studentId:String?=null,
    var name:String?=null,
    var email:String?=null,
    var phone:String?=null,
    var department:String?=null,
    var batch:String?=null,
    var roomNo:String?=null,
    var isCommitteeMember:Boolean?=false,
    var dueAmount:Double?=null,
    var key:String?=null,
    var profilePic:String?=null,
    var password:String?=null,
    var mealCode:String?=null,
    var isStart:Boolean?=null,
    var isMutton:Boolean?=null,
    )
