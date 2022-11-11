package ru.art2000.calculator.currency.remote.cbr

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

internal interface CbrAPI {

    @Headers("Content-Type: text/xml", "Accept-Charset: utf-8")
    @GET("/scripts/XML_daily.asp")
    suspend fun getDailyCurrencies(): CurrenciesList

    @Headers("Content-Type: text/xml", "Accept-Charset: utf-8")
    @GET("/scripts/XML_daily.asp")
    suspend fun getCurrenciesOnDate(@Query("date_req") date: String): CurrenciesList

}