package com.example.plugins

import com.example.entities.NoteEntity
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/") {
//            println("Uri : ${call.request.uri}")
//            println("Headers : ${call.request.headers.names()}")
//            call.respondText("Hello Ktor ")
            val userResponse = User("123@gmail.com", "123")
            call.respond(userResponse)
        }
        get("/checkparameter/{page}") {
            val pageParameter = call.parameters["page"]

            call.respondText("youre on page number : $pageParameter")
        }
        get("/filedownload") {
            val file = File("src/main/files/demonSlayer.png")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName, "downloadableImage.png"
                ).toString()
            )
            call.respondFile(file)
        }
        get("/fileopen") {
            val file = File("src/main/files/itachi.jpg")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Inline.withParameter(
                    ContentDisposition.Parameters.FileName, "downloadableImage.png"
                ).toString()
            )
            call.respondFile(file)
        }

//        post("/login") {
//            val user = call.receive<User>()
//            print(user)
//            call.respondText("you are login ")
//        }

//        get("/notes"){
//            call.respond(NoteEntity)
//        }
    }
}
