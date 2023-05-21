//package com.common.data.network
//
//import com.utility.Encrypt
//import okhttp3.Interceptor
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.Response
//import okhttp3.ResponseBody.Companion.toResponseBody
//import org.json.JSONArray
//import org.json.JSONObject
//import timber.log.Timber
//
///**
// * Created by Keval on 23-03-2020.
// */
//class DecryptionInterceptor : Interceptor {
//
//    private val encrypt = Encrypt()
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val response: Response = chain.proceed(chain.request())
//        if (response.isSuccessful) {
//            val newResponse = response.newBuilder()
//            var contentType = response.headers["Content-Type"]
//            if (contentType.isNullOrEmpty()) {
//                contentType = "application/json"
//            }
//            val responseString = response.body?.string()
//
//            if (!responseString.isNullOrEmpty()) {
//                try {
//                    val respJsonObject = JSONObject(responseString)
//
//                    val value = respJsonObject.optString("value")
//                    val mac = respJsonObject.optString("mac")
//
//                    try {
//                        if (value.isNotEmpty() && mac.isNotEmpty()) {
//                            val decryptedString = encrypt.decrypt(value, mac).orEmpty().trim()
//                            Timber.tag("OkHttp").i("decrypted Response: $decryptedString")
//                            if (decryptedString.startsWith("[") && decryptedString.endsWith("]"))
//                                newResponse.body(
//                                    JSONArray(decryptedString).toString()
//                                        .toResponseBody(contentType.toMediaTypeOrNull())
//                                )
//                            else
//                                newResponse.body(
//                                    JSONObject(decryptedString).toString()
//                                        .toResponseBody(contentType.toMediaTypeOrNull())
//                                )
//
//                            return newResponse.build()
//                        }
//                    } catch (e: Exception) {
//                    }
//
//                    newResponse.body(respJsonObject.toString().toResponseBody(contentType.toMediaTypeOrNull()))
//                    return newResponse.build()
//                } catch (e: Exception) {
//                }
//            }
//        }
//        return response
//    }
//}