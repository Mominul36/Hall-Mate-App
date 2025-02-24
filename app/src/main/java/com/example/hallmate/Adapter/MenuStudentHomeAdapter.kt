package com.example.hallmate.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallmate.Activity.MStudentProfileActivity
import com.example.hallmate.Model.Menu
import com.example.hallmate.Model.Student
import com.example.hallmate.R

class MenuStudentHomeAdapter(
    private val menuList: ArrayList<Menu>,
) : RecyclerView.Adapter<MenuStudentHomeAdapter.MenuStudentHomeViewHolder>() {

    class MenuStudentHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtname: TextView = itemView.findViewById(R.id.txtname)
        val txtquantity: TextView = itemView.findViewById(R.id.txtquantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuStudentHomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_student_home, parent, false)
        return MenuStudentHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuStudentHomeViewHolder, position: Int) {
        val menu = menuList[position]
        holder.txtname.text = "${menu.name}"
        holder.txtquantity.text = "${menu.quantity}"
    }

    override fun getItemCount(): Int = menuList.size
}
