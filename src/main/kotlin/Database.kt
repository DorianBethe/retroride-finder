import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object Database {
    val dataSource: HikariDataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DATABASE_URL")
                ?: "jdbc:postgresql://localhost:5432/retroride"
            username = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "postgres"
            driverClassName = "org.postgresql.Driver"
        }
        HikariDataSource(config)
    }

    fun init() {
        try {
            dataSource.connection.use { conn ->
                conn.createStatement().execute(
                    """
                    CREATE TABLE IF NOT EXISTS search_queries (
                        id SERIAL PRIMARY KEY,
                        query TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT NOW()
                    );
                    """.trimIndent()
                )
            }
            println("Database initialized successfully.")
        } catch (e: Exception) {
            println("Database connection failed (this is okay for local test): ${e.message}")
        }
    }

    fun logQuery(query: String) {
        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement("INSERT INTO search_queries (query) VALUES (?)").use { stmt ->
                    stmt.setString(1, query)
                    stmt.executeUpdate()
                }
            }
        } catch (e: Exception) {
            println("Failed to log query: ${e.message}")
        }
    }
}
