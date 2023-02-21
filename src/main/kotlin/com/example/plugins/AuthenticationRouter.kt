package com.example.plugins

import com.example.db.DatabaseConnection
import com.example.entities.NoteResponse
import com.example.entities.UserCredential
import com.example.entities.UserEntity
import com.example.entities.UserLogin
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Application.authenthicationRouters() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    routing {
        post("/register") {
            val userCredentials = call.receive<UserCredential>()

            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "User should be greater than or equal to 3 and password has ben equal 8"
                    )
                )
                return@post
            }

            val username = userCredentials.username.toLowerCase()
            val password = userCredentials.hashedPassword()

            // CHECK IF USERNAME ALREADY EXIST

            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    it[UserEntity.username]
                }
                .firstOrNull()

            if (user != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "user already exist, please try other username")
                )
                return@post
            }

            db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
            }

            call.respond(
                HttpStatusCode.Created,
                NoteResponse(success = true, data = "User has been succesfully created")
            )
        }

        post("/login") {
            val userCredentials = call.receive<UserCredential>()
            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "User should be greater than or equal to 3 and password has ben equal 8"
                    )
                )
                return@post
            }

            val username = userCredentials.username.toLowerCase()
            val password = userCredentials.password

            //C HECK IF USER EXIST

            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val id = it[UserEntity.id]!!
                    val username = it[UserEntity.username]!!
                    val password = it[UserEntity.password]!!
                    UserLogin(id, username, password)
                }.firstOrNull()
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "Invalid username or password")
                )
                return@post
            }
            val doesPasswordMatch = BCrypt.checkpw(password, user.password)
            if (!doesPasswordMatch) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "Invalid username or password")
                )
                return@post
            }

            val token = tokenManager.generateJWTToken(user)
            call.respond(HttpStatusCode.OK,NoteResponse(success = true, data = token))

        }

        authenticate {
            get ("/me"){
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val userId = principal!!.payload.getClaim("userId").asInt()
                call.respondText("Hello, $username with id: $userId")
            }
        }
    }
}