package br.desafio.fretebras

import retrofit2.Response
import retrofit2.http.GET

interface FretebrasService {

    @GET("freights")
    suspend fun getUsers(): Response<List<Freight>>
}