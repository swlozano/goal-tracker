package com.alejandro.time.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.alejandro.time.R
import com.alejandro.time.model.Goal
import com.alejandro.time.store.DbHelper
import com.alejandro.time.store.SqlQuery
import kotlin.collections.ArrayList

class GoalActivity : AppCompatActivity() {

    private var button: Button? = null
    private var editText: EditText? = null
    private var sqlQuery:SqlQuery? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goal_layout)
        sqlQuery=SqlQuery(this)
        setupSave()
        setupListView()
    }

    override fun onResume() {
        setupListView()
        super.onResume()
    }

    fun startTaskActivity(idGoal:Int, goal:String) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra("idGoal", idGoal)
            putExtra("goal", goal)
        }
        startActivity(intent)
    }

    fun onSelectActividad(idActividad:Int, goal:String) {
        startTaskActivity(idActividad, goal)
    }

    fun setupSave(){
        editText = findViewById(R.id.editTxtGoal) as EditText
        button = findViewById(R.id.btnAdd) as Button
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(!editText!!.text.toString().trim().equals("")) {
                    sqlQuery?.saveGoal(editText!!.text.toString())
                    editText!!.text.clear()
                    setupListView()
                }
            }
        })
    }

    fun setupListView() {
        var listView: ListView = findViewById<ListView>(R.id.goal_listVw)
        val list = sqlQuery!!.getGoals()
        for (i in 0 until list.size){
            list[i].name = list[i].name
        }
        val listItems = arrayOfNulls<String>(list.size)
        for (i in 0 until list.size) {
            val recipe = list[i]
            listItems[i] = recipe.name
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
            onSelectActividad(list.get(position).id, list.get(position).name)
        }
    }




}
