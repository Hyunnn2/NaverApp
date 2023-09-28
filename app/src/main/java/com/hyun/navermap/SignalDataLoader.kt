package com.hyun.navermap

import android.content.res.Resources
import org.json.JSONObject
import java.io.InputStream

class SignalDataLoader(private val resources: Resources) {
    fun loadSignalData(): List<Signal> {
        val signalDataList = mutableListOf<Signal>()

        // JSON 파일을 읽어오기
        val rawResourceId = resources.getIdentifier("data", "raw", "com.hyun.navermap")
        val inputStream: InputStream = resources.openRawResource(rawResourceId)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        // JSON 데이터 파싱
        val jsonObject = JSONObject(jsonString)

        // 0부터 69까지의 신호등 데이터 가져오기
        for (i in 0 until 67) {
            val signalObject = jsonObject.getJSONObject("Signal").getJSONObject(i.toString())
            val signalData = Signal(
                No = signalObject.getString("No"),
                latitude = signalObject.getDouble("latitude"),
                longitude = signalObject.getDouble("longitude"),
                captionText = signalObject.getString("captionText"),
                etc = signalObject.getString("etc"),
                time = parseTimeInfo(signalObject.getJSONObject("time"))
            )
            signalDataList.add(signalData)
        }

        return signalDataList
    }

    private fun parseTimeInfo(timeObject: JSONObject): Map<String, TimeInfo> {
        val timeInfoMap = mutableMapOf<String, TimeInfo>()

        // 0부터 69까지의 신호등 데이터 가져오기(time)
        for (key in timeObject.keys()) {
            val timeInfoObject = timeObject.getJSONObject(key)
            val timeInfo = TimeInfo(
                period = timeInfoObject.getInt("period"),
                StartTime = timeInfoObject.getInt("StartTime"),
                onTime = timeInfoObject.getInt("onTime")
            )
            timeInfoMap[key] = timeInfo
        }
        return timeInfoMap
    }
}