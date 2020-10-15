package com.alejandro.time.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.alejandro.time.R
import com.alejandro.time.model.Task
import com.alejandro.time.store.DbHelper
import com.alejandro.time.store.SqlQuery
import kotlin.collections.ArrayList

class TaskActivity : AppCompatActivity() {

    private var arrTaskIds: IntArray = IntArray(0)
    private var button: Button? = null
    private var editText: EditText? = null
    private var txtGoal: TextView? = null
    private var editTxtGoal: EditText? = null
    private var imgBtnCheck: ImageButton? = null
    private var imgBtnEdit: ImageButton? = null
    var countDelete: Int = 0;

    var idGoal: Int? = 0
    var goal: String? = ""
    var sqlQuery: SqlQuery? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_layout)
        sqlQuery = SqlQuery(this)
        getExtras()
        setupView()
        setupSave()
        setupListView()
    }

    override fun onResume() {
        setupListView()
        super.onResume()
    }

    fun getExtras() {
        idGoal = intent.getIntExtra("idGoal", 0)
        goal = intent.getStringExtra("goal")
    }

    fun startDateActivity(idTask: Int, task: String) {
        val intent = Intent(this, DateActivity::class.java).apply {
            putExtra("idTask", idTask)
            putExtra("goal", goal)
            putExtra("task", task)
        }
        startActivity(intent)
    }

    fun setupView() {
        editTxtGoal = findViewById(R.id.editTxtGoal) as EditText
        txtGoal = findViewById(R.id.txtGoal) as TextView
        imgBtnCheck = findViewById(R.id.imgBtnCheck) as ImageButton
        imgBtnEdit = findViewById(R.id.imgBtnEdit) as ImageButton
        setTextGoal()
    }

    fun setTextGoal() {
        txtGoal!!.setText(goal)
        editTxtGoal!!.setText(goal)
    }

    fun setupSave() {
        editText = findViewById(R.id.editTxtTask) as EditText
        button = findViewById(R.id.btnAdd) as Button
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!editText!!.text.toString().trim().equals("")) {
                    saveActividad(editText!!.text.toString(), idGoal)
                    editText!!.text.clear()
                    setupListView()
                }
            }
        })
    }

    fun setupListView() {
        var listView: ListView = findViewById<ListView>(R.id.task_listVw)
        val list = getTasks(idGoal)
        val listItems = arrayOfNulls<String>(list.size)
        arrTaskIds = IntArray(listItems.size)
        for (i in 0 until list.size) {
            val recipe = list[i]
            listItems[i] = recipe.nameTask
            arrTaskIds[i] = recipe.id
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
            startDateActivity(list.get(position).id, list.get(position).nameTask)
        }
    }

    fun saveActividad(actividad: String, idGoal: Int?): Long? {
        val dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(dbHelper.TASK_NAME_COL, actividad)
            put("idGoal", idGoal)
        }
        val newRowId = db?.insert(dbHelper.TASK_TABLE, null, values)
        return newRowId!!
    }

    fun getTasks(idGoal: Int?): ArrayList<Task> {
        var dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.TASK_TABLE} where idGoal = ${idGoal}", null
        )
        val list = arrayListOf<Task>()
        while (cursor.moveToNext()) {
            var task = Task()
            task.id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
            task.nameTask =
                cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.TASK_NAME_COL))

            list.add(task)
        }
        return list
    }


    fun onClickDelete(v: View) {
        countDelete++;
        if (countDelete > 1) {
            executeDelete()
            this.finish()
        } else {
            Toast.makeText(this, "Presiona una vez más para eliminar", Toast.LENGTH_LONG).show()
        }
    }

    fun executeDelete() {
        sqlQuery!!.deleteDates(arrTaskIds)
        idGoal?.let { sqlQuery!!.deleteTasks(it) }
        idGoal?.let { sqlQuery!!.deleteGoal(it) }
    }

    fun onClickBtnEdit(v: View) {
        toggleEditComponents(v.id)
        updateGoal(v.id)
    }

    fun toggleEditComponents(idView: Int) {

        if (idView == R.id.imgBtnEdit) {
            imgBtnCheck!!.visibility = View.VISIBLE
            editTxtGoal!!.visibility = View.VISIBLE

            imgBtnEdit!!.visibility = View.GONE
            txtGoal!!.visibility = View.GONE

        } else {
            imgBtnCheck!!.visibility = View.GONE
            editTxtGoal!!.visibility = View.GONE

            imgBtnEdit!!.visibility = View.VISIBLE
            txtGoal!!.visibility = View.VISIBLE
        }
    }

    fun updateGoal(idView: Int) {
        if (idView == R.id.imgBtnCheck) {
            goal = editTxtGoal!!.text.toString()
            sqlQuery!!.updateGoal(idGoal, goal)
            setTextGoal()
        }
    }

}
