//package com.common.data.network
//
//import com.google.gson.Gson
//import com.utility.Encrypt
//import okhttp3.Interceptor
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import okhttp3.internal.http.HttpMethod
//import okio.Buffer
//import timber.log.Timber
//import java.io.IOException
//
///**
// * Created by Keval on 23-03-2020.
// */
//class EncryptionInterceptor : Interceptor {
//
//    private val encrypt = Encrypt()
//
//    private val contentType = "application/json; charset=utf-8".toMediaTypeOrNull()
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//
//        var request = chain.request()
//        if (!HttpMethod.requiresRequestBody(request.method)) {
//            return chain.proceed(request)
//        }
//
//        val rawBody = request.body
//
//        try {
//            val rawBodyString = requestBodyToString(rawBody)
//            Timber.tag("OkHttp").i("rawData Request: $rawBodyString")
//            val encryptedBody = Gson().toJson(encrypt.encrypt(rawBodyString))
//            Timber.tag("OkHttp").i("encrypted Request: $encryptedBody")
//            val requestBody = encryptedBody.toRequestBody(contentType)
//
//            request = request.newBuilder()
//                .header("Content-Type", requestBody.contentType().toString())
//                .header("Content-Length", requestBody.contentLength().toString())
//                .method(request.method, requestBody).build()
//
//        } catch (e: Exception) {
//        }
//        return chain.proceed(request)
//    }
//
//    @Throws(IOException::class)
//    fun requestBodyToString(requestBody: RequestBody?): String {
//        if (requestBody == null) return ""
//        val buffer = Buffer()
//        requestBody.writeTo(buffer)
//        return buffer.readUtf8()
//    }
//}