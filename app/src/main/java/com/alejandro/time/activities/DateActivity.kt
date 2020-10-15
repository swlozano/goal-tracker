package com.alejandro.time.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.time.R
import com.alejandro.time.store.SqlQuery
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateActivity : AppCompatActivity() {


    private var button: Button? = null
    private var btnHideCal: Button? = null
    private var txtMsg: TextView? = null
    private var dateList: ListView? = null
    private var calendarView: CalendarView? = null
    private var txtTask: TextView? = null
    private var txtGoalHead: TextView? = null
    private var editTxtTask: EditText? = null
    private var imgBtnCheck: ImageButton? = null
    private var imgBtnEdit: ImageButton? = null
    var countDelete: Int = 0
    var goal: String? = ""
    var task: String? = ""
    var sqlQuery: SqlQuery? = null
    var idTask: Int = 0
    var formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
    var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.date_layout)
        sqlQuery = SqlQuery(this)
        getExtras()
        setupCalendar()
        setupView()
        setupSave()
    }

    fun getExtras() {
        idTask = intent.getIntExtra("idTask", 0)
        goal = intent.getStringExtra("goal")
        task = intent.getStringExtra("task")
    }

    fun setupView() {
        txtMsg = findViewById(R.id.txtMsg) as TextView
        txtGoalHead = findViewById(R.id.txtGoalHead) as TextView
        txtTask = findViewById(R.id.txtGoal) as TextView
        txtGoalHead!!.setText("Goal: " + goal)
        editTxtTask = findViewById(R.id.editTxtGoal) as EditText
        setTextTask()
        imgBtnCheck = findViewById(R.id.imgBtnCheck) as ImageButton
        imgBtnEdit = findViewById(R.id.imgBtnEdit) as ImageButton
        btnHideCal = findViewById(R.id.btnHideCalendar) as Button
        btnHideCal!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (calendarView!!.visibility == View.GONE)
                    calendarView!!.visibility = View.VISIBLE
                else
                    calendarView!!.visibility = View.GONE
            }
        })
        dateList = findViewById(R.id.dateList) as ListView
        fillListView()
    }

    fun setTextTask() {
        txtTask!!.setText(task)
        editTxtTask!!.setText(task)
    }

    fun fillListView() {
        val list = sqlQuery!!.getDates(idTask)
        val listItems = arrayOfNulls<String>(list.size)

        for (i in 0 until list.size) {
            val recipe = list[i]
            listItems[i] = recipe.msg
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        dateList!!.adapter = adapter
        dateList!!.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
            sqlQuery!!.deleteDateById(list.get(position).id)
            fillListView()
        }
    }

    fun setupSave() {
        button = findViewById(R.id.btnAdd) as Button
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!selectedDate.equals("")) {
                    var d = sqlQuery!!.getDate(idTask, selectedDate)
                    if (d == null) {
                        sqlQuery!!.saveDate(selectedDate, idTask)
                    }
                    updateMsg()
                    fillListView()
                }
            }
        })
    }


    fun setupCalendar() {
        calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val strSelectedDate = "" + year + "-" + (month + 1) + "-" + dayOfMonth
            selectedDate = LocalDate.parse(strSelectedDate, formatter).toString()
            updateMsg()
        }
    }

    fun updateMsg() {
        val dateDb = sqlQuery!!.getDate(idTask, selectedDate)
        if (dateDb != null) {
            changeTxtMsg(selectedDate + " :)")
        } else {
            changeTxtMsg(selectedDate)
        }
    }

    fun changeTxtMsg(msg: String) {
        txtMsg!!.setText(msg)
    }


    fun onClickDelete(v: View) {

        countDelete++;
        if (countDelete > 1) {
            idTask?.let { sqlQuery!!.deleteDates(it) }
            idTask?.let { sqlQuery!!.deleteTask(it) }
            this.finish()
        } else {
            Toast.makeText(this, "Presiona una vez más para eliminar", Toast.LENGTH_LONG).show()
        }

    }

    fun onClickBtnEdit(v: View) {
        toggleEditComponents(v.id)
        updateGoal(v.id)
    }

    fun toggleEditComponents(idView: Int) {

        if (idView == R.id.imgBtnEdit) {
            imgBtnCheck!!.visibility = View.VISIBLE
            editTxtTask!!.visibility = View.VISIBLE

            imgBtnEdit!!.visibility = View.GONE
            txtTask!!.visibility = View.GONE

        } else {
            imgBtnCheck!!.visibility = View.GONE
            editTxtTask!!.visibility = View.GONE

            imgBtnEdit!!.visibility = View.VISIBLE
            txtTask!!.visibility = View.VISIBLE
        }
    }

    fun updateGoal(idView: Int) {
        if (idView == R.id.imgBtnCheck) {
            task = editTxtTask!!.text.toString()
            sqlQuery!!.updateTask(idTask, task)
            setTextTask()
        }
    }


}
