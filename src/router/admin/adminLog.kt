package com.ntihs_fk.router.admin

import com.ntihs_fk.util.apiFrameworkFun
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Route.adminLog() {

    get("/api/log/list") {
        val fileList = File("./logs").list()
        call.respond(apiFrameworkFun(fileList))
    }

    delete("/api/log/{file}") {
        val fileName = call.parameters["file"]
        val file = File("./logs/$fileName")

        if (file.isFile && file.exists()) {
            file.delete()
            call.respond(apiFrameworkFun(null))
        } else throw NoSuchFileException(file)
    }

    static {
        files("./logs")
    }
}