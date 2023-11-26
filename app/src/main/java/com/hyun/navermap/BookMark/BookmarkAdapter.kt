package com.hyun.navermap.BookMark

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.hyun.navermap.R
import com.hyun.navermap.calculate.Signal

class BookmarkAdapter(
    context: Context,
    signals: List<Signal>,
    private val onSignalSelectedListener: com.hyun.navermap.fragments.BookMarkFragment.OnSignalSelectedListener
) : ArrayAdapter<Signal>(context, 0, signals) {

    private val sharedPreferences =
        context.getSharedPreferences("BookmarkPreferences", Context.MODE_PRIVATE)

    private var showBookmarkedSignals: Boolean = false
    private var originalSignals: List<Signal> = ArrayList(signals)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.item_bookmark, parent, false
            )
        }

        val currentSignal = getItem(position)
        val signalId = currentSignal?.No

        val signalNumberTextView: TextView = listItemView!!.findViewById(R.id.signalNumberTextView)
        val signalLocationTextView: TextView = listItemView.findViewById(R.id.signalLocationTextView)
        val bookmarkButton: ImageButton = listItemView.findViewById(R.id.bookmarkButton)

        // 클릭 이벤트 리스너 설정
        listItemView.setOnClickListener {
            val selectedSignal = getItem(position)
            onSignalSelectedListener.onSignalSelected(selectedSignal!!)
        }

        // 북마크 상태 설정
        setBookmarkButtonState(bookmarkButton, signalId)

        // "즐겨찾기" 라디오 버튼이 선택되었을 때, 북마크된 신호등만 표시
        if (showBookmarkedSignals) {
            val isBookmarked = sharedPreferences.getBoolean(signalId, false)
            if (!isBookmarked) {
                // 북마크가 되어 있지 않으면 뷰를 숨깁니다.
                listItemView.visibility = View.GONE

                return listItemView
            } else {
                // 북마크된 신호등이면 뷰를 보이도록 설정
                listItemView.visibility = View.VISIBLE
            }
        } else {
            // "즐겨찾기" 라디오 버튼이 선택되지 않았을 때는 모든 뷰를 보이도록 설정
            listItemView.visibility = View.VISIBLE
        }

        // 북마크 상태에 따라 뷰 업데이트
        signalNumberTextView.text = currentSignal?.No
        signalLocationTextView.text = currentSignal?.captionText

        // 클릭 이벤트 리스너 설정
        bookmarkButton.setOnClickListener {
            // 클릭 시 이미지 변경 및 북마크 상태 저장
            val isBookmarked = toggleBookmarkState(bookmarkButton, signalId)
            // 여기에서 즐겨찾기 상태에 따라 데이터를 처리하거나 다른 작업을 수행할 수 있습니다.
        }

        return listItemView
    }

    // 필터 상태를 업데이트하는 메서드
    fun setShowBookmarkedSignals(showBookmarked: Boolean) {
        showBookmarkedSignals = showBookmarked
        if (showBookmarked) {
            // "즐겨찾기" 라디오 버튼이 선택되었을 때, 북마크된 신호등만 필터링하여 리스트 업데이트
            val bookmarkedSignals = originalSignals.filter { signal ->
                sharedPreferences.getBoolean(signal.No, false)
            }
            clear()
            addAll(bookmarkedSignals)
        } else {
            // "즐겨찾기" 라디오 버튼이 선택되지 않았을 때는 원래의 리스트로 복원
            clear()
            addAll(originalSignals)
        }
        notifyDataSetChanged()
    }

    private fun toggleBookmarkState(button: ImageButton, signalId: String?): Boolean {
        // 현재 상태 확인
        val isBookmarked = sharedPreferences.getBoolean(signalId, false)

        // 상태 변경
        val newBookmarkState = !isBookmarked

        // 클릭 시 이미지 변경
        val newImageResource =
            if (newBookmarkState) R.drawable.bookmark_on
            else R.drawable.ic_bookmark

        button.setImageResource(newImageResource)

        // 즐겨찾기 상태를 SharedPreferences에 저장
        sharedPreferences.edit().putBoolean(signalId, newBookmarkState).apply()

        return newBookmarkState
    }

    private fun setBookmarkButtonState(button: ImageButton, signalId: String?) {
        // 현재 상태 확인
        val isBookmarked = sharedPreferences.getBoolean(signalId, false)

        // 이미지 변경
        val imageResource =
            if (isBookmarked) R.drawable.bookmark_on
            else R.drawable.ic_bookmark

        button.setImageResource(imageResource)
    }
}