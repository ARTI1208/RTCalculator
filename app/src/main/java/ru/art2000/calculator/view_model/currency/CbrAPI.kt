package ru.art2000.calculator.view_model.currency

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.art2000.calculator.model.currency.CurrenciesList

interface CbrAPI {

    @Headers("Content-Type: text/xml", "Accept-Charset: utf-8")
    @GET("/scripts/XML_daily.asp")
    suspend fun getDailyCurrencies(): CurrenciesList

    @Headers("Content-Type: text/xml", "Accept-Charset: utf-8")
    @GET("/scripts/XML_daily.asp")
    suspend fun getCurrenciesOnDate(@Query("date_req") date: String): CurrenciesList

}