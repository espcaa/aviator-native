package eu.espcaa.aviator.scripts
import eu.espcaa.aviator.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginResponse(
    val message: String,
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

interface LoginApi {
    @POST("/api/sessions/getRefreshToken")
    suspend fun getRefreshToken(
        @Body request: LoginRequest
    ): LoginResponse
}

