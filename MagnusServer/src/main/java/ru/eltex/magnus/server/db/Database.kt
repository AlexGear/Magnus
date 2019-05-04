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

    override fun insertEmployee(employee: Employee?): Boolean {
        if(employee == null)  throw NullPointerException()

        val sql = "INSERT INTO $EMPLOYEES_TABLE (login, password, name, department_id," +
                " job_name, phone_number, email) VALUES (?, ?, ?, ?, ?, ?, ?)"
        return try {
            with(employee) {
                executeUpdate(sql, login, password, name, department.id, jobName, phoneNumber, email)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun updateEmployee(employee: Employee?): Boolean {
        if(employee == null) throw NullPointerException()

        val sql = "UPDATE $EMPLOYEES_TABLE SET password = ?, name = ?, department_id = ?," +
                " job_name = ?, phone_number = ?, email = ? WHERE login = ?"
        return try {
            with(employee) {
                executeUpdate(sql, password, name, department.id, jobName, phoneNumber, email, login)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun removeEmployeeByLogin(login: String?): Boolean {
        if(login == null) throw NullPointerException()

        val sql = "DELETE FROM $EMPLOYEES_TABLE WHERE login = ?"
        return try {
            executeUpdate(sql, login)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
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

    override fun insertDepartmentAndAssignId(department: Department?): Boolean {
        if (department == null) throw NullPointerException()

        val sql = "INSERT INTO $DEPARTMENTS_TABLE (name) VALUES (?)"
        return try {
            executeUpdate(sql, department.name) { insertedId ->
                department.id = insertedId
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun updateDepartment(department: Department?): Boolean {
        if (department == null) throw NullPointerException()

        val sql = "UPDATE $DEPARTMENTS_TABLE SET name = ? WHERE id = ?"
        return try {
            executeUpdate(sql, department.name, department.id)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun removeDepartmentById(id: Int): Boolean {
        val sql = "DELETE FROM $DEPARTMENTS_TABLE WHERE id = ?"
        return try {
            executeUpdate(sql, id)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun getAllViewers(): List<Viewer>? {
        val sql = "SELECT login, password, name FROM $VIEWERS_TABLE"
        return try {
            selectMany(sql) { rs ->
                Viewer(rs["login"], rs["password"], rs["name"])
            }
        } catch (e: SQLException) {
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
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun insertViewer(viewer: Viewer?): Boolean {
        if(viewer == null) throw NullPointerException()

        val sql = "INSERT INTO $VIEWERS_TABLE (login, password, name) VALUES (?, ?, ?)"
        return try {
            executeUpdate(sql, viewer.login, viewer.password, viewer.name)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun updateViewer(viewer: Viewer?): Boolean {
        if(viewer == null) throw NullPointerException()

        val sql = "UPDATE $VIEWERS_TABLE SET password = ?, name = ? WHERE login = ?"
        return try {
            executeUpdate(sql, viewer.password, viewer.name, viewer.login)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun removeViewerByLogin(login: String?): Boolean {
        if(login == null) throw NullPointerException()

        val sql = "DELETE FROM $VIEWERS_TABLE WHERE login = ?"
        return try {
            executeUpdate(sql, login)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun getAllOfflineStreamers(): List<OfflineStreamer>? {
        val sql = "SELECT login, last_seen FROM $OFFLINE_STREAMERS_TABLE"
        return try {
            selectMany(sql) { rs ->
                OfflineStreamer(rs["login"], rs["last_seen"])
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
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    override fun insertOfflineStreamer(offlineStreamer: OfflineStreamer?): Boolean {
        if(offlineStreamer == null) throw NullPointerException()

        val sql = "INSERT INTO $OFFLINE_STREAMERS_TABLE (login, last_seen) VALUES (?, ?)"
        return try {
            executeUpdate(sql, offlineStreamer.login, offlineStreamer.lastSeen)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun updateOfflineStreamer(offlineStreamer: OfflineStreamer?): Boolean {
        if(offlineStreamer == null) throw NullPointerException()

        val sql = "UPDATE $OFFLINE_STREAMERS_TABLE SET last_seen = ? WHERE login = ?"
        return try {
            executeUpdate(sql, offlineStreamer.lastSeen, offlineStreamer.login)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun removeOfflineStreamerByLogin(login: String?): Boolean {
        if(login == null) throw NullPointerException()

        val sql = "DELETE FROM $OFFLINE_STREAMERS_TABLE WHERE login = ?"
        return try {
            executeUpdate(sql, login)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

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

    override fun updateAdmin(admin: Admin?): Boolean {
        if(admin == null) throw NullPointerException()

        val sql = "UPDATE $ADMIN_TABLE SET login = ?, password = ?"
        return try {
            executeUpdate(sql, admin.login, admin.password)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    private fun openConnection(): Connection {
        return DriverManager.getConnection(connectionURL, databaseLogin, databasePassword)
    }

    private inline fun <T> executeQuery(sql: String, vararg args: Any, action: (ResultSet) -> T) : T {
        return openConnection().use { connection ->
            if (args.isNotEmpty())
                connection.prepareStatement(sql, *args).use { it.executeQuery().use(action) }
            else
                connection.createStatement().use { it.executeQuery(sql).use(action) }
        }
    }

    private inline fun executeUpdate(sql: String, vararg args: Any, lastInsertIdAction: (Int) -> Unit) : Boolean {
        return openConnection().use { connection ->
            val result = if (args.isNotEmpty())
                connection.prepareStatement(sql, *args).use { 1 == it.executeUpdate() }
            else
                connection.createStatement().use { 1 == it.executeUpdate(sql) }

            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT LAST_INSERT_ID()").use { rs ->
                    rs.first()
                    lastInsertIdAction(rs.getInt(1))
                }
            }
            result
        }
    }

    private fun executeUpdate(sql: String, vararg args: Any) : Boolean {
        return openConnection().use { connection ->
            if (args.isNotEmpty())
                connection.prepareStatement(sql, *args).use { 1 == it.executeUpdate() }
            else
                connection.createStatement().use { 1 == it.executeUpdate(sql) }
        }
    }

    private inline fun <T> selectFirstOrNull(sql: String, vararg args: Any, selector: (ResultSet) -> T) : T? {
        return executeQuery(sql, *args) { rs ->
            if (rs.first()) selector(rs) else null
        }
    }

    private inline fun <T> selectMany(sql: String, selector: (ResultSet) -> T) : List<T> {
        return executeQuery(sql) { rs ->
            val result = mutableListOf<T>()
            while (rs.next()) result += selector(rs)
            result
        }
    }

    private inline operator fun <reified T> ResultSet.get(s: String) = this.getObject(s) as T

    private fun Connection.prepareStatement(sql: String, vararg args: Any) : PreparedStatement {
        val statement = this.prepareStatement(sql)
        for (i in args.indices) {
            statement.setObject(i + 1, args[i])
        }
        return statement
    }
}