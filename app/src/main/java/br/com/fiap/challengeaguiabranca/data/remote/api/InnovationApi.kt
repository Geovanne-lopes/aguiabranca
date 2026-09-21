package br.com.fiap.challengeaguiabranca.data.remote.api

import br.com.fiap.challengeaguiabranca.data.remote.dto.AiInsightResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ApiPageDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.AuthResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.CreateIdeaRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.CreateProjectRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.CreateSuggestionRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.DailyInsightResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.GuidelineHistoryResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.GuidelineResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.GuidelineWriteRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.IdeaResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.LeaderDashboardDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.LoginRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.LogoutRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ManagerDashboardDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.NotificationsResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ProjectResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.RankingsResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.RefreshRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.RegisterRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.ResetPasswordRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.StrategiesResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.SuggestionResponseDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.UpdateIdeaStatusRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.UpdateProfileRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.UpdateProjectRequestDto
import br.com.fiap.challengeaguiabranca.data.remote.dto.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface InnovationApi {

    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body body: LogoutRequestDto): Response<Unit>

    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequestDto): Response<Unit>

    @GET("api/v1/auth/me")
    suspend fun me(): Response<UserResponseDto>

    @PATCH("api/v1/users/me")
    suspend fun updateMe(@Body body: UpdateProfileRequestDto): Response<UserResponseDto>

    @GET("api/v1/users")
    suspend fun listUsers(
        @Query("role") role: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<ApiPageDto<UserResponseDto>>

    @GET("api/v1/guidelines")
    suspend fun listGuidelines(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<ApiPageDto<GuidelineResponseDto>>

    @GET("api/v1/guidelines/{id}")
    suspend fun getGuideline(@Path("id") id: String): Response<GuidelineResponseDto>

    @POST("api/v1/guidelines")
    suspend fun createGuideline(@Body body: GuidelineWriteRequestDto): Response<GuidelineResponseDto>

    @PUT("api/v1/guidelines/{id}")
    suspend fun updateGuideline(
        @Path("id") id: String,
        @Body body: GuidelineWriteRequestDto
    ): Response<GuidelineResponseDto>

    @DELETE("api/v1/guidelines/{id}")
    suspend fun deleteGuideline(@Path("id") id: String): Response<Unit>

    @GET("api/v1/guidelines/{id}/history")
    suspend fun guidelineHistory(
        @Path("id") id: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<ApiPageDto<GuidelineHistoryResponseDto>>

    @GET("api/v1/ideas")
    suspend fun listIdeas(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("status") status: String? = null,
        @Query("authorId") authorId: String? = null
    ): Response<ApiPageDto<IdeaResponseDto>>

    @POST("api/v1/ideas")
    suspend fun createIdea(@Body body: CreateIdeaRequestDto): Response<IdeaResponseDto>

    @GET("api/v1/ideas/{id}")
    suspend fun getIdea(@Path("id") id: String): Response<IdeaResponseDto>

    @PUT("api/v1/ideas/{id}")
    suspend fun updateIdea(
        @Path("id") id: String,
        @Body body: CreateIdeaRequestDto
    ): Response<IdeaResponseDto>

    @DELETE("api/v1/ideas/{id}")
    suspend fun deleteIdea(@Path("id") id: String): Response<Unit>

    @PATCH("api/v1/ideas/{id}/status")
    suspend fun updateIdeaStatus(
        @Path("id") id: String,
        @Body body: UpdateIdeaStatusRequestDto
    ): Response<IdeaResponseDto>

    @GET("api/v1/projects")
    suspend fun listProjects(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("status") status: String? = null
    ): Response<ApiPageDto<ProjectResponseDto>>

    @POST("api/v1/projects")
    suspend fun createProject(@Body body: CreateProjectRequestDto): Response<ProjectResponseDto>

    @GET("api/v1/projects/by-idea/{ideaId}")
    suspend fun getProjectByIdea(@Path("ideaId") ideaId: String): Response<ProjectResponseDto>

    @GET("api/v1/projects/{id}")
    suspend fun getProject(@Path("id") id: String): Response<ProjectResponseDto>

    @PUT("api/v1/projects/{id}")
    suspend fun updateProject(
        @Path("id") id: String,
        @Body body: UpdateProjectRequestDto
    ): Response<ProjectResponseDto>

    @DELETE("api/v1/projects/{id}")
    suspend fun deleteProject(@Path("id") id: String): Response<Unit>

    @GET("api/v1/suggestions")
    suspend fun listSuggestions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<ApiPageDto<SuggestionResponseDto>>

    @POST("api/v1/suggestions")
    suspend fun createSuggestion(@Body body: CreateSuggestionRequestDto): Response<SuggestionResponseDto>

    @GET("api/v1/notifications")
    suspend fun notifications(): Response<NotificationsResponseDto>

    @GET("api/v1/insights/daily")
    suspend fun dailyInsight(): Response<DailyInsightResponseDto>

    @GET("api/v1/dashboard/manager")
    suspend fun managerDashboard(): Response<ManagerDashboardDto>

    @GET("api/v1/dashboard/leader")
    suspend fun leaderDashboard(): Response<LeaderDashboardDto>

    @GET("api/v1/dashboard/strategies")
    suspend fun strategyReturns(): Response<StrategiesResponseDto>

    @GET("api/v1/dashboard/rankings")
    suspend fun rankings(@Query("period") period: String): Response<RankingsResponseDto>

    @GET("api/v1/ai/insights/latest")
    suspend fun latestAiInsight(): Response<AiInsightResponseDto>

    @POST("api/v1/ai/insights")
    suspend fun generateAiInsight(): Response<AiInsightResponseDto>
}
