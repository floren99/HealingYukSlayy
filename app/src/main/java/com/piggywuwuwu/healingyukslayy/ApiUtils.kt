package com.piggywuwuwu.healingyukslayy

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object ApiUtils {
    private const val BASE_URL = "https://ubaya.xyz/native/160422069/"

    fun makeApiCall(
        context: Context,
        endpoint: String,
        method: Int = Request.Method.GET,
        params: JSONObject? = null,
        onSuccess: (response: JSONObject) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        var url = BASE_URL + endpoint

        if (method == Request.Method.DELETE && params != null) {
            val query = params.keys().asSequence()
                .map { "$it=${params.getString(it)}" }
                .joinToString("&")
            url += "?$query"
        }

        val request = object : JsonObjectRequest(
            method,
            url,
            if (method != Request.Method.DELETE) params else null,
            { response -> onSuccess(response) },
            { error -> onError(error.message ?: "Unknown error") }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        queue.add(request)
    }
}