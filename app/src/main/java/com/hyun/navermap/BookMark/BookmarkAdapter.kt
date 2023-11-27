package com.hyun.navermap.BookMark

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hyun.navermap.R
import com.hyun.navermap.calculate.Signal
import com.hyun.navermap.fragments.BookMarkFragment

class BookmarkAdapter(
    context: Context,
    signals: List<Signal>,
    private val onSignalSelectedListener: BookMarkFragment.OnSignalSelectedListener,
    private val userUid: String // Firebase에서 사용자 UID를 가져오는 방법에 따라 할당
) : ArrayAdapter<Signal>(context, 0, signals) {

    private val bookmarksRef = FirebaseDatabase.getInstance().getReference("bookmarks").child(userUid)

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

        // 북마크 버튼 클릭 이벤트 처리
        bookmarkButton.setOnClickListener {
            val isBookmarked = toggleBookmarkState(bookmarkButton, signalId)
            if (isBookmarked) {
                addBookmark(signalId)
            } else {
                removeBookmark(signalId)
            }
        }

        signalNumberTextView.text = currentSignal?.No
        signalLocationTextView.text = currentSignal?.captionText

        return listItemView
    }

    // 북마크 추가
    private fun addBookmark(signalId: String?) {
        signalId?.let {
            bookmarksRef.child(signalId).setValue(true)
        }
    }

    // 북마크 삭제
    private fun removeBookmark(signalId: String?) {
        signalId?.let {
            bookmarksRef.child(signalId).removeValue()
        }
    }

    // 북마크 상태를 토글하는 함수
    private fun toggleBookmarkState(button: ImageButton, signalId: String?): Boolean {
        val isBookmarked = button.tag as? Boolean ?: false
        val newBookmarkState = !isBookmarked

        val newImageResource =
            if (newBookmarkState) R.drawable.bookmark_on
            else R.drawable.ic_bookmark

        button.setImageResource(newImageResource)
        button.tag = newBookmarkState // 버튼의 상태를 저장하는 태그를 사용하여 상태 유지

        return newBookmarkState
    }

    private fun setBookmarkButtonState(button: ImageButton, signalId: String?) {
        if (signalId != null) {
            bookmarksRef.child(signalId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isBookmarked = snapshot.exists()
                    val imageResource =
                        if (isBookmarked) R.drawable.bookmark_on
                        else R.drawable.ic_bookmark

                    button.setImageResource(imageResource)
                    button.tag = isBookmarked // 버튼의 태그에 상태를 저장하여 상태 유지
                }

                override fun onCancelled(error: DatabaseError) {
                    // 처리하지 않음
                }
            })
        }
    }

    fun setShowBookmarkedSignals(showBookmarked: Boolean) {
        var showBookmarkedSignals = showBookmarked
        if (showBookmarked) {
            // Firebase에서 사용자가 북마크한 신호등 ID 가져오기
            bookmarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookmarkedSignals = originalSignals.filter { signal ->
                        snapshot.child(signal.No).exists()
                    }
                    clear()
                    addAll(bookmarkedSignals)
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                }
            })
        } else {
            // 모든 신호등 표시
            clear()
            addAll(originalSignals)
            notifyDataSetChanged()
        }
    }
}