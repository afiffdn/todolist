package com.example

import com.example.entities.NoteEntity
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import org.ktorm.database.Database
import org.ktorm.dsl.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        configureRouting()


//

        // INSERT VALUE TO DATABASE


        // READ VALUE ON DATABASE
//        var note = database.from(NoteEntity)
//            .select()
//
//        for (row in note){
//            println("${row[NoteEntity.id]} : ${row[NoteEntity.note]}")
//        }

        //UPDATE VALUE IN DATABASE
//        database.update(NoteEntity){
//            set(it.note, "learning ktor")
//            where { it.id eq 1 }
//        }

        //DELETE VALUE IN DATABASE
//        database.delete(NoteEntity){
//            it.note eq "learning ktor"
//        }

    .start(wait = true)
       
}

fun Application.module() {
    auth()
    configureSerialization()
    contactUsModule()
    configureRouting()
    notesRouting()
    authenthicationRouters()

}

