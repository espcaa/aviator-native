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

data class SessionTokenResponse(
    val message: String,
    val token : String,
    val success: Boolean
)
data class SessionTokenRequest(
    val refreshToken: String
)

interface SessionTokenApi {
    @POST("/api/sessions/login")
    suspend fun getSessionToken(
        @Body request: SessionTokenRequest
    ): SessionTokenResponse
}

data class CreateFlightRequest(
    val sessionToken: String,
    val arrivalCode: String,
    val departureCode: String,
    val departureDate: String,
    val airlineCode: String
)

data class Position(
    val lat: Double?,
    val lon: Double?
)

data class Positions(
    val departure: Position,
    val arrival: Position
)

data class CreateFlightResponse(
    val message: String,
    val success: Boolean,
    val positions: Positions,
    val duration : Number,
    val flightId: String,
)

interface CreateFlightApi {
    @POST("/api/flights/createFlight")
    suspend fun createFlight(
        @Body request: CreateFlightRequest
    ): CreateFlightResponse
}

data class DeleteFlightRequest(
    val sessionToken: String,
    val flightId: String
)

data class DeleteFlightResponse(
    val message: String,
    val success: Boolean
)

interface DeleteFlightApi {
    @POST("/api/flights/deleteFlight")
    suspend fun deleteFlight(
        @Body request: DeleteFlightRequest
    ): DeleteFlightResponse
}

data class GetFlightsRequest(
    val sessionToken: String
)

data class GetFlightsResponse(
    val message : String,
    val flights: List<Flight>,
    val success: Boolean
)

data class Flight(
    val flightId: Number,
    val departureCode: String,
    val arrivalCode: String,
    val departureDate: String,
    val duration: Number,
    val departureAirportLat: Number,
    val departureAirportLon: Number,
    val arrivalAirportLat: Number,
    val arrivalAirportLon: Number,
    val airlineCode: String
)

interface GetFlightsApi {
    @POST("/api/flights/getFlights")
    suspend fun getFlights(
        @Body request: GetFlightsRequest
    ): GetFlightsResponse
}

data class GetAirlinesRequest(
    val sessionToken: String,
    val searchString: String = "",
    val searchLimit: Int = 10,

)

data class GetAirlinesResponse(
    val message: String,
    val airlines: List<Airline>,
)

data class Airline(
    val id: String,
    val name: String,
    val code: String,
    val country: String,
    val hasLogo: Boolean,
)

data class GetAirportsResponse(
    val message: String,
    val airports: List<Airport>,
)

data class Airport(
    val id: String,
    val name: String,
    val code: String,
    val country: String
)

interface GetAirlinesApi {
    @POST("/api/airlines/getAirlines")
    suspend fun getAirlines(
        @Body request: GetAirlinesRequest
    ): GetAirlinesResponse
}

interface GetAirportsApi {
    @POST("/api/airports/getAirports")
    suspend fun getAirports(
        @Body request: GetAirlinesRequest
    ): GetAirportsResponse
}