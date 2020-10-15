package com.alejandro.time.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.time.R
import com.alejandro.time.store.DbHelper
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*var id = saveActividad("Nadar")
        saveFecha(LocalDateTime.now(), LocalDateTime.now(), id)*/
        setupSave()
        setupListView()
    }



    fun onSelectActividad(idActividad:Long) {
        var lastRegister = getLastRegister()
        updateHoraActividad(lastRegister.id)
        saveFecha(LocalDateTime.now(), LocalDateTime.now(),idActividad)
        setupListView()
    }


    private var button: Button? = null
    private var editText: EditText? = null

    fun setupSave(){

        editText = findViewById(R.id.editTxtActividad) as EditText
        button = findViewById(R.id.btnAdd) as Button
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                saveActividad(editText!!.text.toString())
                setupListView()
            }
        })

    }

    fun setupListView() {
        var listView: ListView = findViewById<ListView>(R.id.activi_listVw)
        val list = getActividades()
        for (i in 0 until list.size){
            list[i].actividad = list[i].actividad + "\n"  + getActividadesByDay(list[i].id)
        }
        val listItems = arrayOfNulls<String>(list.size)
        for (i in 0 until list.size) {
            val recipe = list[i]
            listItems[i] = recipe.actividad
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->

            Log.i("Click" ,"****click " + list.get(position).id)
            onSelectActividad(list.get(position).id)
        }
    }

    fun saveActividad(actividad: String): Long? {

        val dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase


        val values = ContentValues().apply {
            put(dbHelper.NOMBRE_ACTIVIDAD_COL, actividad)

        }

        val newRowId = db?.insert(dbHelper.ACTIVIDAD_TABLE, null, values)
        Log.d("new ACTIVIDAD", "*****id: ${newRowId}")
        return newRowId!!

    }

    fun saveFecha(fechaIni: LocalDateTime, fechaFin: LocalDateTime, idActividad: Long?) {


        val dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase


        val values = ContentValues().apply {
            put(dbHelper.FECHA_INICIO_COL, formatDate(fechaIni))
            put(dbHelper.FECHA_FIN_COL, formatDate(fechaFin))
            put(dbHelper.HORA_INICIO_COL, formatHour(fechaIni))
            put(dbHelper.HORA_FIN_COL, formatHour(fechaFin))
            put(dbHelper.ID_ACTIVIDAD_COL, idActividad)

        }

        val newRowId = db?.insert(dbHelper.FECHA_ACTIVIDAD_TABLE, null, values)
        Log.d("new FECHA", "*****id: ${newRowId}")

    }

    fun getLastRegister(): FechaActividad {
        var dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase


        val cursor = db.rawQuery(
            "SELECT * \n" +
                    "    FROM    ${dbHelper.FECHA_ACTIVIDAD_TABLE}\n" +
                    "    WHERE   ID = (SELECT MAX(ID)  FROM ${dbHelper.FECHA_ACTIVIDAD_TABLE});",
            null
        )

        var fecha = FechaActividad()


        while (cursor.moveToNext()) {
            fecha.id = cursor.getLong(cursor.getColumnIndexOrThrow("ID"))
            fecha.fi = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.FECHA_INICIO_COL))
            fecha.ff = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.FECHA_FIN_COL))
            fecha.hi = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.HORA_INICIO_COL))
            fecha.hf = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.HORA_FIN_COL))
        }

        return fecha

    }

    fun getActividades(): ArrayList<Actividad> {
        var dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase


        val cursor = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.ACTIVIDAD_TABLE}",null

        )

        val list = arrayListOf<Actividad>()

        while (cursor.moveToNext()) {
            var actividad = Actividad()
            actividad.id = cursor.getLong(cursor.getColumnIndexOrThrow("ID"))
            actividad.actividad =
                cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.NOMBRE_ACTIVIDAD_COL))

            list.add(actividad)
        }

        return list

    }

    fun getActividadesByDay(idActividad: Long): String {
        var dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase


        val cursor = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.FECHA_ACTIVIDAD_TABLE} WHERE ${dbHelper.ID_ACTIVIDAD_COL} = ${idActividad} and ${dbHelper.FECHA_INICIO_COL} = ${formatDate(
                LocalDateTime.now())}",null
        )

        val cursor1 = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.FECHA_ACTIVIDAD_TABLE} WHERE ${dbHelper.ID_ACTIVIDAD_COL} = ${idActividad} AND ${dbHelper.FECHA_INICIO_COL} = '${formatDate(
                LocalDateTime.now())}' ORDER BY ID ASC",null
        )

        val list = arrayListOf<FechaActividad>()

        while (cursor1.moveToNext()) {
            var fecha = FechaActividad()
            fecha.id = cursor1.getLong(cursor.getColumnIndexOrThrow("ID"))
            fecha.fi = cursor1.getString(cursor.getColumnIndexOrThrow(dbHelper.FECHA_INICIO_COL))
            fecha.ff = cursor1.getString(cursor.getColumnIndexOrThrow(dbHelper.FECHA_FIN_COL))
            fecha.hi = cursor1.getString(cursor.getColumnIndexOrThrow(dbHelper.HORA_INICIO_COL))
            fecha.hf = cursor1.getString(cursor.getColumnIndexOrThrow(dbHelper.HORA_FIN_COL))

            list.add(fecha)
        }

        var diff:String=""
        if (list!=null && !list.isEmpty()){
            if(list.size==1){
                diff =getHours(list[0])
            }else{
                list[0].ff = list[list.size-1].ff
                list[0].hf = list[list.size-1].hf
                diff =getHours(list[0])
            }
        }

        return diff

    }

    class FechaActividad {
        var id: Long = 0
        var fi: String = ""
        var ff: String = ""
        var hi: String = ""
        var hf: String = ""
    }

    class Actividad {
        var id: Long = 0
        var actividad: String = ""

    }

    fun updateHoraActividad(id: Long) {

        var dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase

        // New value for one column
        val title = "MyNewTitle"
        val values = ContentValues().apply {
            put(dbHelper.HORA_FIN_COL, formatHour(LocalDateTime.now()))
        }

        // Which row to update, based on the title
        val selection = "ID LIKE ?"
        val selectionArgs = arrayOf(id.toString())
        val count = db.update(
            dbHelper.FECHA_ACTIVIDAD_TABLE,
            values,
            selection,
            selectionArgs
        )


    }

    private fun formatDate(fecha: LocalDateTime): String {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return fecha.format(formatter)
    }

    private fun formatHour(fecha: LocalDateTime): String {
        var formatter = DateTimeFormatter.ofPattern("H:m:s")
        return fecha.format(formatter)
    }

    private fun getHours(fechaActividad: FechaActividad): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd H:m:s")
        val fechaInicial: Date = dateFormat.parse("${fechaActividad.fi} ${fechaActividad.hi} ")
        val fechaFinal: Date = dateFormat.parse("${fechaActividad.ff} ${fechaActividad.hf} ")
        var diferencia = ((fechaFinal.getTime() - fechaInicial.getTime()) / 1000)
        var dias = 0
        var horas = 0
        var minutos = 0
        if (diferencia > 86400) {
            dias = Math.floor(diferencia / 86400.toDouble()).toInt()
            diferencia = diferencia - dias * 86400
        }
        if (diferencia > 3600) {
            horas = Math.floor(diferencia / 3600.toDouble()).toInt()
            diferencia = diferencia - horas * 3600
        }
        if (diferencia > 60) {
            minutos = Math.floor(diferencia / 60.toDouble()).toInt()
            diferencia = diferencia - minutos * 60
        }
        return "$dias dias, $horas horas, $minutos minutos y $diferencia segundos de diferencia"

    }
}
