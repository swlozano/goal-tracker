package com.alejandro.time.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.time.R
import com.alejandro.time.store.DbHelper

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


abstract class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)
        //var id = saveActividad("Nadar")
        //saveFecha(LocalDateTime.now(), LocalDateTime.now(),id )
        //getLastRegister()
    }

    fun saveActividad(actividad: String): Long? {

        val dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase


        val values = ContentValues().apply {
            put(dbHelper.NOMBRE_ACTIVIDAD_COL, actividad)

        }

        val newRowId = db?.insert(dbHelper.ACTIVIDAD_TABLE, null, values)
        Log.d("new ACTIVIDAD","*****id: ${newRowId}")
        return newRowId!!

    }

    fun saveFecha(fechaIni: LocalDateTime, fechaFin:LocalDateTime, idActividad: Long?){

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s")
        Log.d("FECHA INI","*****id: ${fechaIni.format(formatter)}")
        //getHours(fechaIni.format(formatter), fechaFin.format(formatter))
        val dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase


        val values = ContentValues().apply {
            put(dbHelper.FECHA_INICIO_COL, fechaIni.toString())
            put(dbHelper.FECHA_FIN_COL, fechaFin.toString())
            put(dbHelper.ID_ACTIVIDAD_COL, idActividad)

        }

        val newRowId = db?.insert(dbHelper.FECHA_ACTIVIDAD_TABLE, null, values)
        Log.d("new FECHA","*****id: ${newRowId}")

    }

    fun getLastRegister(){
        var dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase


        val cursor = db.rawQuery("SELECT * \n" +
                "    FROM    ${dbHelper.FECHA_ACTIVIDAD_TABLE}\n" +
                "    WHERE   ID = (SELECT MAX(ID)  FROM ${dbHelper.FECHA_ACTIVIDAD_TABLE});",null)

        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow("ID"))
                Log.d("","*****itemid ${itemId}");
            }
        }

    }


    fun getHours(fechaIni: String, fechaFin: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd H:m:s")
        val fechaInicial: Date = dateFormat.parse(fechaIni)
        val fechaFinal: Date = dateFormat.parse(fechaFin)
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
        Log.d("diferencias: ","****Hay $dias dias, $horas horas, $minutos minutos y $diferencia segundos de diferencia")
    }


}
