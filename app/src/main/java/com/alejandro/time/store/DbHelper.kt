package com.alejandro.time.store

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {


    val ACTIVIDAD_TABLE = "actividad";
    val NOMBRE_ACTIVIDAD_COL = "nombre";

    //Tables

    val CURRENT_TABLE = "current";
    val GOAL_TABLE = "goal";
    val TASK_TABLE = "task";
    val DATE_TABLE = "date";

    val FECHA_ACTIVIDAD_TABLE = "fecha";

    //COLS
    val FECHA_INICIO_COL = "fechaini";
    val FECHA_FIN_COL = "fechafin";
    public val ID_FECHA_ACTIVIDAD_COL = "idFecha";
    public val GOAL_NAME_COL = "goal";
    public val TASK_NAME_COL = "task";
    public val DATE_COL = "date";
    public val TASK_WAS_MAKE_COL = "wasTaskMake";
    val HORA_INICIO_COL = "horaini";
    val HORA_FIN_COL = "horafin";
    val ID_ACTIVIDAD_COL = "actividad_id";

    //SQL QUERIES
    private val SQL_CREATE_ACTIVIDAD_TBL =
        "CREATE TABLE ${ACTIVIDAD_TABLE} (" +
                "ID INTEGER PRIMARY KEY," +
                "${NOMBRE_ACTIVIDAD_COL} TEXT)";

    private val SQL_CREATE_FECHA_TBL =
        "CREATE TABLE ${FECHA_ACTIVIDAD_TABLE} (" +
                "ID INTEGER PRIMARY KEY, ${ID_ACTIVIDAD_COL} INTEGER," +
                "${FECHA_INICIO_COL} TEXT," +
                "${FECHA_FIN_COL} TEXT, ${HORA_INICIO_COL} TEXT, ${HORA_FIN_COL} TEXT ,FOREIGN KEY( ${ID_ACTIVIDAD_COL}) REFERENCES ${ACTIVIDAD_TABLE}(ID))";

    private val SQL_CREATE_CURRENT_TBL =
        "CREATE TABLE ${CURRENT_TABLE} (" +
                "ID INTEGER PRIMARY KEY," +
                "${ID_FECHA_ACTIVIDAD_COL} INTEGER)";

    private val SQL_CREATE_GOAL_TBL =
        "CREATE TABLE ${GOAL_TABLE} (" +
                "ID INTEGER PRIMARY KEY," +
                "${GOAL_NAME_COL} TEXT)";


    private val SQL_CREATE_TASK_TBL =
        "CREATE TABLE ${TASK_TABLE} (" +
                "ID INTEGER PRIMARY KEY," + "  idGoal INTEGER, " +
                "${TASK_NAME_COL} TEXT)";

    private val SQL_CREATE_DATE_TBL =
        "CREATE TABLE ${DATE_TABLE} (" +
                "ID INTEGER PRIMARY KEY," + " idTask INTEGER, " +
                "${DATE_COL} TEXT, ${TASK_WAS_MAKE_COL} INTEGER)";

    private val SQL_DELETE_ACTIVIDAD = "DROP TABLE IF EXISTS ${ACTIVIDAD_TABLE}"
    private val SQL_DELETE_FECHA_ACTIVIDAD = "DROP TABLE IF EXISTS ${FECHA_ACTIVIDAD_TABLE}"
    private val SQL_DELETE_FECHA_CURRENT = "DROP TABLE IF EXISTS ${CURRENT_TABLE}"
    private val SQL_DELETE_GOAL = "DROP TABLE IF EXISTS ${GOAL_TABLE}"
    private val SQL_DELETE_TASK = "DROP TABLE IF EXISTS ${TASK_TABLE}"
    private val SQL_DELETE_DATE = "DROP TABLE IF EXISTS ${DATE_TABLE}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ACTIVIDAD_TBL)
        db.execSQL(SQL_CREATE_FECHA_TBL)
        db.execSQL(SQL_CREATE_CURRENT_TBL)
        db.execSQL(SQL_CREATE_GOAL_TBL)
        db.execSQL(SQL_CREATE_TASK_TBL)
        db.execSQL(SQL_CREATE_DATE_TBL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ACTIVIDAD)
        db.execSQL(SQL_DELETE_FECHA_ACTIVIDAD)
        db.execSQL(SQL_DELETE_FECHA_CURRENT)
        db.execSQL(SQL_DELETE_GOAL)
        db.execSQL(SQL_DELETE_TASK)
        db.execSQL(SQL_DELETE_DATE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 21
        const val DATABASE_NAME = "GoalTracker.db"
    }
}