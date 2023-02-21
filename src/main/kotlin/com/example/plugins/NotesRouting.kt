package com.example.plugins

import com.example.db.DatabaseConnection
import com.example.entities.Note
import com.example.entities.NoteEntity
import com.example.entities.NoteRequest
import com.example.entities.NoteResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

fun Application.notesRouting() {
    val db = DatabaseConnection.database
    routing {
        get("/notes") {
            val notes = db.from(NoteEntity).select()
                .map {
                    val id = it[NoteEntity.id]
                    val note = it[NoteEntity.note]
                    Note(id ?: -1, note ?: "")
                }
            call.respond(notes)
        }

        post("/notes"){
            val req = call.receive<NoteRequest>()
            val result = db.insert(NoteEntity){
                set(it.note, req.note)
            }

            if (result == 1){
                call.respond(HttpStatusCode.OK,NoteResponse(
                    success = true,
                    data = "Vallues has been succesfully inserted"
                ))
            }else{
                call.respond(HttpStatusCode.BadRequest,NoteResponse(
                    success = false,
                    data = "Failed"
                ))
            }
        }

        get("/notes/{id}"){
             val id = call.parameters["id"]?.toInt() ?: -1

            val note = db.from(NoteEntity)
                .select()
                .where{NoteEntity.id eq id}
                .map {
                    val id = it[NoteEntity.id]!!
                    val note = it[NoteEntity.note]!!
                    Note(id = id, note = note)
                }.firstOrNull()
            if (note == null ){
                call.respond(HttpStatusCode.NotFound,
                    NoteResponse(
                        success = false,
                        data = "could not found id = ${id}"
                    ))
            }else{
                call.respond(HttpStatusCode.OK,
                    NoteResponse(
                        success = true,
                        data = note
                    ))

            }
        }

        put("notes/{id}"){
            val id = call.parameters["id"]?.toInt() ?: -1

            val updateNote = call.receive<NoteRequest>()

            val rowsEffected = db.update((NoteEntity)){
                set(it.note, updateNote.note)
                where {
                    it.id eq id
                }
            }

            if (rowsEffected == 1){
                call.respond(HttpStatusCode.OK,NoteResponse(
                    success = true,
                    data = "note has been update"
                ) )
            }else{
                call.respond(HttpStatusCode.BadRequest,NoteResponse(
                    success = false,
                    data = "failed update"
                ) )
            }
        }

        delete("/notes/{id}"){
            val id = call.parameters["id"]?.toInt() ?: -1

            val deleteNote = db.delete(NoteEntity){
                 it.id eq id
            }
            if (deleteNote == 1){
                call.respond(HttpStatusCode.OK,NoteResponse(
                    success = true,
                    data = "note has been delete"
                ) )
            }else{
                call.respond(HttpStatusCode.BadRequest,NoteResponse(
                    success = false,
                    data = "failed delete"
                ) )
            }
        }
    }
}