package com.example.db

import org.ktorm.database.Database

object DatabaseConnection  {
    val database = Database.connect (
        url = "jdbc:mysql://localhost/notes",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = ""
    )
}