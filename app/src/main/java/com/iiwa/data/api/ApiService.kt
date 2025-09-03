package com.iiwa.data.api

import retrofit2.http.GET

interface ApiService {
    
    // Basic API service interface
    // Add more endpoints here as needed for your app
    
    @GET("health")
    suspend fun healthCheck(): String
}
