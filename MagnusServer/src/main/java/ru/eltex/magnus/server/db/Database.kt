package ru.eltex.magnus.server.db

import ru.eltex.magnus.server.db.dataclasses.*
import ru.eltex.magnus.server.db.storages.*

import java.sql.*

internal class Database(properties: DatabaseProperties) : EmployeesStorage, DepartmentsStorage,
        OfflineStreamersStorage, ViewersStorage, AdminStorage {

    companion object {
        private const val EMPLOYEES_TABLE = "employees"
        private const val DEPARTMENTS_TABLE = "departments"
        private const val VIEWERS_TABLE = "viewers"
        private const val OFFLINE_STREAMERS_TABLE = "offline_streamers"
        private const val ADMIN_TABLE = "admin"
    }

    private val connectionURL = properties.connectionURL
    private val databaseLogin = properties.login
    private val databasePassword = properties.password

    override fun getAdmin(): Admin? {
        val sql = "SELECT login, password FROM $ADMIN_TABLE"
        return try {
            selectFirstOrNull(sql) { rs ->
                Admin(rs["login"], rs["password"])
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getDepartmentById(id: Int): Department? {
        val sql = "SELECT name FROM $DEPARTMENTS_TABLE WHERE id = ?"
        return try {
            selectFirstOrNull(sql, id) { rs ->
                Department(id, rs["name"])
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllDepartments(): List<Department>? {
        val sql = "SELECT id, name FROM $DEPARTMENTS_TABLE"
        return try {
            selectMany(sql) { rs ->
                Department(rs["id"], rs["name"])
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
            selectFirstOrNull(sql, login) { rs ->
                with(Employee()) {
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
            selectMany(sql) { rs ->
                with(Employee()) {
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
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getOfflineStreamerByLogin(login: String): OfflineStreamer? {
        val sql = "SELECT last_seen FROM $OFFLINE_STREAMERS_TABLE WHERE login = ?"
        return try {
            selectFirstOrNull(sql, login) { rs ->
                OfflineStreamer(login, rs["last_seen"])
            }
        }
        catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllOfflineStreamers(): List<OfflineStreamer>? {
        val sql = "SELECT login, last_seen FROM $OFFLINE_STREAMERS_TABLE"
        return try {
            executeQuery(sql) { rs ->
                val result = mutableListOf<OfflineStreamer>()
                while(rs.next()) {
                    result += OfflineStreamer(rs["login"], rs["last_seen"])
                }
                result
            }
        }
        catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getViewerByLogin(login: String): Viewer? {
        val sql = "SELECT password, name FROM $VIEWERS_TABLE WHERE login = ?"
        return try {
            selectFirstOrNull(sql, login) { rs ->
                Viewer(login, rs["password"], rs["name"])
            }
        }
        catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllViewers(): List<Viewer>? {
        val sql = "SELECT login, password, name FROM $VIEWERS_TABLE WHERE login"
        return try {
            selectMany(sql) { rs ->
                Viewer(rs["login"], rs["password"], rs["name"])
            }
        }
        catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    private fun openConnection(): Connection {
        return DriverManager.getConnection(connectionURL, databaseLogin, databasePassword)
    }

    private fun <T> executeQuery(sql: String, vararg args: Any, action: (ResultSet) -> T) : T {
        openConnection().use { connection ->
            if(args.isNotEmpty()) {
                connection.prepareStatement(sql).use { statement ->
                    for (i in args.indices) {
                        statement.setObject(i + 1, args[i])
                    }
                    return statement.executeQuery().use(action)
                }
            }
            else connection.createStatement().use { statement ->
                return statement.executeQuery(sql).use(action)
            }
        }
    }

    private fun <T> selectFirstOrNull(sql: String, vararg args: Any, selector: (ResultSet) -> T) : T? {
        return executeQuery(sql, *args) { rs ->
            if (rs.first())
                selector(rs)
            else
                null
        }
    }

    private fun <T> selectMany(sql: String, selector: (ResultSet) -> T) : List<T> {
        return executeQuery(sql) { rs ->
            val result = mutableListOf<T>()
            while (rs.next()) {
                result += selector(rs)
            }
            result
        }
    }
    
    private inline operator fun <reified T> ResultSet.get(s: String) = this.getObject(s) as T
}