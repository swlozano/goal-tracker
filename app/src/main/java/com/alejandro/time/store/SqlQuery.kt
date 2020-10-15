package com.alejandro.time.store

import android.content.ContentValues
import android.content.Context
import com.alejandro.time.model.DateDb
import com.alejandro.time.model.Goal

class SqlQuery(context_param: Context) {

    val dbHelper: DbHelper = DbHelper(context_param);
    val db = dbHelper.writableDatabase

    fun saveGoal(goal: String): Long? {
        val values = ContentValues().apply {
            put(dbHelper.GOAL_NAME_COL, goal)
        }
        val newRowId = db?.insert(dbHelper.GOAL_TABLE, null, values)
        return newRowId!!
    }

    fun getGoals(): ArrayList<Goal> {

        val cursor = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.GOAL_TABLE}", null
        )
        val list = arrayListOf<Goal>()
        while (cursor.moveToNext()) {
            var goal = Goal()
            goal.id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
            goal.name =
                cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.GOAL_NAME_COL))
            list.add(goal)
        }
        return list
    }


    fun deleteGoal(id: Int) {

        db.execSQL("DELETE FROM ${dbHelper.GOAL_TABLE} WHERE ID = ${id}");
    }

    fun deleteTasks(idGoal: Int) {

        db.execSQL("DELETE FROM ${dbHelper.TASK_TABLE} WHERE idGoal = ${idGoal}");
    }

    fun deleteDateByIdTask(idTask: Int) {
        db.execSQL("DELETE FROM ${dbHelper.DATE_TABLE} WHERE idTask = ${idTask}");
    }

    fun deleteDates(arrTaskIds: IntArray) {
        if (!arrTaskIds.isEmpty() && arrTaskIds.size != 0) {
            for (i in 0 until arrTaskIds.size) {
                deleteDateByIdTask(arrTaskIds[i])
            }
        }
    }

    fun deleteTask(id: Int) {
        db.execSQL("DELETE FROM ${dbHelper.TASK_TABLE} WHERE ID = ${id}");
    }

    fun deleteDates(idTask: Int) {
        db.execSQL("DELETE FROM ${dbHelper.DATE_TABLE} WHERE idTask = ${idTask}");
    }

    fun deleteDateById(id: Int) {
        db.execSQL("DELETE FROM ${dbHelper.DATE_TABLE} WHERE ID = ${id}");
    }

    fun getDates(idTask: Int): ArrayList<DateDb> {
        val cursor = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.DATE_TABLE} where idTask = ${idTask} ORDER BY ${dbHelper.DATE_COL} ASC",
            null
        )
        var dateDB: DateDb? = null
        val list = arrayListOf<DateDb>()
        while (cursor.moveToNext()) {
            dateDB = DateDb();
            dateDB.id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
            dateDB.idTask =
                cursor.getInt(cursor.getColumnIndexOrThrow("idTask"))
            dateDB.wasMake =
                cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.TASK_WAS_MAKE_COL))
            dateDB.date =
                cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.DATE_COL))
            var maked = ""
            if (dateDB.wasMake == 1) {
                maked = " :)"
            }
            dateDB.msg = dateDB.date + maked
            list.add(dateDB)
        }
        return list
    }

    fun saveDate(date: String, idTask: Int?): Long? {

        val values = ContentValues().apply {
            put(dbHelper.DATE_COL, date)
            put("idTask", idTask)
            put(dbHelper.TASK_WAS_MAKE_COL, 1)
        }

        val newRowId = db?.insert(dbHelper.DATE_TABLE, null, values)
        return newRowId!!

    }

    fun getDate(idTask: Int, selectedDate: String): DateDb? {
        val cursor = db.rawQuery(
            "SELECT *  FROM  ${dbHelper.DATE_TABLE} where idTask = ${idTask} AND ${dbHelper.DATE_COL}='${selectedDate}'",
            null
        )
        var task: DateDb? = null
        if (cursor.moveToNext()) {
            task = DateDb();
            task?.let {
                task.id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                task.idTask =
                    cursor.getInt(cursor.getColumnIndexOrThrow("idTask"))
                task.wasMake =
                    cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.TASK_WAS_MAKE_COL))
                task.date =
                    cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.DATE_COL))
            }
        }
        return task
    }

    fun updateDate(dateDb: DateDb, selectedDate: String) {

        if (dateDb.wasMake == 0) {
            dateDb.wasMake = 1;
        } else {
            dateDb.wasMake = 0;
        }
        val values = ContentValues().apply {
            put(dbHelper.TASK_WAS_MAKE_COL, dateDb.wasMake)
        }
        val args =
            arrayOf(selectedDate)

        db.update(dbHelper.DATE_TABLE, values, "${dbHelper.DATE_COL}=?", args)
    }

    fun updateGoal(id:Int?, goal: String?) {

        val values = ContentValues().apply {
            put(dbHelper.GOAL_NAME_COL, goal)
        }
        val args =
            arrayOf(id.toString())

        db.update(dbHelper.GOAL_TABLE, values, "ID=?", args)
    }

    fun updateTask(idTask:Int?, task: String?) {

        val values = ContentValues().apply {
            put(dbHelper.TASK_NAME_COL, task)
        }
        val args =
            arrayOf(idTask.toString())

        db.update(dbHelper.TASK_TABLE, values, "ID=?", args)
    }

}