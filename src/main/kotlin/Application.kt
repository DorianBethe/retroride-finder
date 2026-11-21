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
        get("/fetch-api") {
            try {
                // Using a reliable government API for car makes
                val apiUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/getallmakes?format=json"
                val response = java.net.URL(apiUrl).readText()
                
                // Parse the JSON response
                val jsonObject = org.json.JSONObject(response)
                val resultsArray = jsonObject.getJSONArray("Results")
                val firstCar = resultsArray.getJSONObject(0)
                
                val make = firstCar.getString("Make_Name")
                val model = "Unknown"
                val year = 2024

                // Save to database
                Database.saveApiCar(make, model, year)

                call.respondText("Successfully fetched data from API and saved to database! Data: $year $make $model")
            } catch (e: Exception) {
                call.respondText("Failed to fetch data: ${e.message}")
            }
        }

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
