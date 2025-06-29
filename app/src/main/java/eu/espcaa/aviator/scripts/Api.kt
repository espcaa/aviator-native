package eu.espcaa.aviator.scripts
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

data class OtpRequest(
    val email: String
)

data class OtpResponse(
    val message: String,
)

interface OtpApi {
    @POST("/api/otp/generate")
    suspend fun generateOtp(
        @Body request: OtpRequest
    ): OtpResponse
}

data class EmailExistsResponse(
    val exists: Boolean
)

data class EmailExistsRequest(
    val email: String
)

interface EmailExistsApi {
    @POST("/api/users/checkEmail")
    suspend fun emailExists(
        @Body request: EmailExistsRequest
    ): EmailExistsResponse
}

data class RegisterResponse(
    val message: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val otp: String
)

interface CreateAccountApi {
    @POST("/api/users/createUser")
    suspend fun createAccount(
        @Body request: RegisterRequest
    ): RegisterResponse
}

