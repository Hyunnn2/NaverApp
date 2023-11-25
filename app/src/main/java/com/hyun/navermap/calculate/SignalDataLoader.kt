package com.hyun.navermap.calculate

import android.content.res.Resources
import org.json.JSONObject
import java.io.InputStream

/**
 * 해당 클래스는 안드로이드 스튜디오 내의 .json 파일에 접근한다.
 * 접근한 파일에 신호등 데이터에서 필요한 데이터를 읽어온다.
 * 읽어온 데이터를 signalDataList에 저장하는 함수를 가지고 있다.
 *
 */
class SignalDataLoader(private val resources: Resources) {
    // 하는 일 : .json에 접근해 데이터를 읽어온다.
    fun loadSignalData() : List<Signal> {

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

    // 하는 일 : 신호등의 시간데이터를 읽어온다.
    private fun parseTimeInfo(timeObject: JSONObject) : Map<String, TimeInfo> {
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