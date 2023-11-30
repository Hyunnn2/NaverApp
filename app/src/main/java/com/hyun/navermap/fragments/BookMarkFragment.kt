package com.hyun.navermap.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RadioGroup
import com.hyun.navermap.bookMark.BookMarkAdapter
import com.hyun.navermap.R
import com.hyun.navermap.calculate.Signal
import com.hyun.navermap.calculate.SignalDataLoader
import com.google.firebase.auth.FirebaseAuth

class BookMarkFragment : Fragment() {

    private lateinit var signalAdapter: BookMarkAdapter
    private lateinit var onSignalSelectedListener: OnSignalSelectedListener
    private lateinit var userUid: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSignalSelectedListener) {
            onSignalSelectedListener = context
        } else {
            throw ClassCastException("$context must implement OnSignalSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_book_mark, container, false)

        val listView: ListView = rootView.findViewById(R.id.signalListView)
        val radioGroup: RadioGroup = rootView.findViewById(R.id.radioGroup)

        // Firebase 사용자 UID 가져오기
        val auth = FirebaseAuth.getInstance()
        userUid = auth.currentUser?.uid ?: ""

        // 신호등 데이터 로드 (ViewModel이나 다른 소스에서 이 데이터를 가져올 수 있습니다)
        val signalDataLoader = SignalDataLoader(resources)
        val signalDataList: List<Signal> = signalDataLoader.loadSignalData()

        // 어댑터 생성 및 설정
        signalAdapter = BookMarkAdapter(requireContext(), signalDataList, onSignalSelectedListener, userUid)
        listView.adapter = signalAdapter

        // 라디오 그룹 리스너 설정하여 라디오 버튼 클릭을 처리
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonAll -> {
                    signalAdapter.setShowBookmarkedSignals(false)
                }
                R.id.radioButtonFavorites -> {
                    signalAdapter.setShowBookmarkedSignals(true)
                }
            }
        }

        return rootView
    }

    interface OnSignalSelectedListener {
        fun onSignalSelected(signal: Signal)
    }
}