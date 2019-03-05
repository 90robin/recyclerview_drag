package com.jayce.recyclerdrag

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.view.LayoutInflater
import org.jetbrains.anko.find
import android.app.Activity
import android.graphics.Rect
import android.os.Vibrator




class MainActivity : AppCompatActivity() {
    var contentList = ArrayList<String>()
    var myAdapter:RcAdapter?=null
    var mVibrator:Vibrator?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mVibrator = getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator//震动
        initData()
        initView()
    }

    private fun initData() {
        for(position in 0..10){
            contentList.add("$position ***")
        }
    }

    private fun initView() {
        val spanCount = 5 // 3 columns
        val spacing = 20 // 50px
        val includeEdge = false
        myAdapter = RcAdapter()
        rc_view.layoutManager = GridLayoutManager(this,spanCount)
        rc_view.adapter = myAdapter
        rc_view.addItemDecoration(GridSpacingItemDecoration(spanCount,spacing,includeEdge))
        var helper =ItemTouchHelper(MyTouchHelperCbk())
        helper.attachToRecyclerView(rc_view)
    }

    inner class RcAdapter : RecyclerView.Adapter<RcHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RcHolder {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.item_rc,p0,false)
            return RcHolder(view)

        }

        override fun getItemCount(): Int {
            return  contentList.size
        }

        override fun onBindViewHolder(p0: RcHolder, p1: Int) {
            p0.contentTxt?.text = contentList[p1]
        }
    }

    inner class RcHolder : RecyclerView.ViewHolder {
        var contentTxt: TextView? = null

        constructor(itemView: View) : super(itemView) {
            contentTxt =itemView.find(R.id.content_txt)
        }
    }
    inner class MyTouchHelperCbk:ItemTouchHelper.Callback(){
        override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
            var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
           // var swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags,0)
        }

        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
            Collections.swap(contentList,p1.adapterPosition,p2.adapterPosition);
            myAdapter?.notifyItemMoved(p1.adapterPosition,p2.adapterPosition);
            return false
        }

        override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
            //侧滑事件
            contentList?.removeAt(p1)
            myAdapter?.notifyItemRemoved(p0.adapterPosition)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                mVibrator?.vibrate(60)
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

    }

    inner class GridSpacingItemDecoration(private val spanCount: Int //列数
                                          , private val spacing: Int //间隔
                                          , private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}
