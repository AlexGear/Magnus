package ru.eltex.magnus.server.db

import ru.eltex.magnus.server.db.dataclasses.*
import ru.eltex.magnus.server.db.storages.*
import java.lang.UnsupportedOperationException

import java.sql.*
import java.util.ArrayList
import java.util.Collections

internal class Database(properties: DatabaseProperties) : EmployeesStorage, DepartmentsStorage,
        OfflineStreamersStorage, ViewersStorage, AdminStorage {

    companion object {
        private const val EMPLOYEES_TABLE = "employees"
        private const val DEPARTMENTS_TABLE = "departments"
        private const val VIEWERS_TABLE = "viewers"
        private const val OFFLINE_STREAMERS_TABLE = "offline_streamers"
        private const val ADMIN_TABLE = "admin"
    }

    private val connectionURL: String = properties.connectionURL
    private val login: String = properties.login
    private val password: String = properties.password

    override fun getAdmin(): Admin? {
        val sql = "SELECT login, password FROM $ADMIN_TABLE"
        return try {
            executeQuery(sql) { rs ->
                if (rs.first()) Admin(rs["login"], rs["password"]) else null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getDepartmentById(id: Int): Department? {
        val sql = "SELECT name FROM $DEPARTMENTS_TABLE WHERE id = ?"
        return try {
            executeQueryArgs(sql, id) { rs ->
                if (rs.first()) Department(id, rs["name"]) else null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllDepartments(): List<Department>? {
        val sql = "SELECT id, name FROM $DEPARTMENTS_TABLE"
        return try {
            executeQuery(sql) { rs ->
                val result = mutableListOf<Department>()
                while (rs.next()) {
                    result += Department(rs["id"], rs["name"])
                }
                result
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getEmployeeByLogin(login: String): Employee? {
        val sql = "SELECT password, $EMPLOYEES_TABLE.name," +
                " department_id, $DEPARTMENTS_TABLE.name," +
                " job_name, phone_number," +
                " email FROM $EMPLOYEES_TABLE" +
                " LEFT JOIN $DEPARTMENTS_TABLE" +
                " ON $DEPARTMENTS_TABLE.id = department_id" +
                " WHERE login = ?"
        return try {
            executeQueryArgs(sql, login) { rs ->
                if (!rs.first()) {
                    null
                }
                else with(Employee()) {
                    this.login = login
                    password = rs["password"]
                    name = rs["$EMPLOYEES_TABLE.name"]
                    department = Department(rs["department_id"], rs["$DEPARTMENTS_TABLE.name"])
                    jobName = rs["job_name"]
                    phoneNumber = rs["phone_number"]
                    email = rs["email"]
                    this
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllEmployees(): List<Employee>? {
        val sql = "SELECT login, password, $EMPLOYEES_TABLE.name," +
                " department_id, $DEPARTMENTS_TABLE.name," +
                " job_name, phone_number," +
                " email FROM $EMPLOYEES_TABLE" +
                " LEFT JOIN $DEPARTMENTS_TABLE" +
                " ON $DEPARTMENTS_TABLE.id = department_id"
        return try {
            executeQuery(sql) { rs ->
                val result = mutableListOf<Employee>()
                while(rs.next()) {
                    result += with(Employee()) {
                        login = rs["login"]
                        password = rs["password"]
                        name = rs["$EMPLOYEES_TABLE.name"]
                        department = Department(rs["department_id"], rs["$DEPARTMENTS_TABLE.name"])
                        jobName = rs["job_name"]
                        phoneNumber = rs["phone_number"]
                        email = rs["email"]
                        this
                    }
                }
                result
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getOfflineStreamerByLogin(login: String): OfflineStreamer? {
        return null
    }

    override fun getAllOfflineStreamers(): List<OfflineStreamer>? {
        return null
    }

    override fun getViewerByLogin(login: String): Viewer? {
        return null
    }

    override fun getAllViewers(): List<Viewer>? {
        return null
    }

    @Throws(SQLException::class)
    private fun openConnection(): Connection {
        return DriverManager.getConnection(connectionURL, login, password)
    }

    @Throws(SQLException::class)
    private fun <T> executeQuery(sql: String, action: (ResultSet) -> T) : T {
        openConnection().use { connection ->
            connection.createStatement().use { statement ->
                return statement.executeQuery(sql).use(action)
            }
        }
    }

    @Throws(SQLException::class)
    private fun <T> executeQueryArgs(sql: String, vararg args: Any, action: (ResultSet) -> T) : T {
        openConnection().use { connection ->
            connection.prepareStatement(sql).use { statement ->
                for (i in args.indices) {
                    statement.setObject(i + 1, args[i])
                }
                return statement.executeQuery().use(action)
            }
        }
    }

    private inline operator fun <reified T> ResultSet.get(s: String) : T {
        return this.getObject(s) as T
    }
}