import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    Database.init()
    
    routing {
        get("/") {
            call.respondText(
                """
                <html>
                  <body>
                    <h1>RetroRide Finder</h1>
                    <p>Enter a make/model/color to search for (e.g., 1987 Toyota Land Cruiser, Beige):</p>
                    <form action="/echo" method="get">
                      <input type="text" name="query" placeholder="1987 Toyota Land Cruiser" />
                      <button type="submit">Search</button>
                    </form>
                  </body>
                </html>
                """.trimIndent(),
                ContentType.Text.Html
            )
        }

        get("/echo") {
            val query = call.request.queryParameters["query"] ?: "No input provided"
            Database.logQuery(query)
            call.respondText(
                """
                <html>
                  <body>
                    <h1>Your Search</h1>
                    <p>You searched for: <strong>$query</strong></p>
                    <a href="/">Back</a>
                  </body>
                </html>
                """.trimIndent(),
                ContentType.Text.Html
            )
        }
    }
}
