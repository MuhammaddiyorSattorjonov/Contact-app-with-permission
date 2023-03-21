package com.example.contactapp.swipe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

abstract class MySwipeHelper(
    val context: Context,
    private val recyclerView: RecyclerView,
    internal var buttonWith: Int,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var buttonList: MutableList<MyButton>? = null

    lateinit var gestureDetector: GestureDetector
    var swipePosition = -1
    var swipeThreshold = 0.5f
    var buttonBuffer: MutableMap<Int, MutableList<MyButton>>
    lateinit var removerQueue: LinkedList<Int>

    abstract fun instantiaveMyButton(
        viewHolder: RecyclerView.ViewHolder,
        buffer: MutableList<MyButton>,
    )

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            for (button in buttonList!!) {
                if (button.onClick(e!!.x, e!!.y)) {
                    break
                }
            }
            return true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private var onTouchListener = View.OnTouchListener { _, motionEvent ->
        println("Kod ishlayapti")
        if (swipePosition < 0) return@OnTouchListener false
        val point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
        val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
        val swipeItem = swipeViewHolder!!.itemView
        val rect = Rect()
        swipeItem.getGlobalVisibleRect(rect)
        println("kod ishlayapti oxiri")

        if (motionEvent.action == MotionEvent.ACTION_DOWN ||
            motionEvent.action == MotionEvent.ACTION_MOVE ||
            motionEvent.action == MotionEvent.ACTION_UP
        ) {
            println("kod ishlayapti oxiri")
            if (rect.top < point.y && rect.bottom > point.y) {
                gestureDetector.onTouchEvent(motionEvent)
            } else {
                removerQueue.add(swipePosition)
                swipePosition = -1
                recoverSwipeItem()
            }
        }
        println("kod ishlayapti oxiri")
        false
    }

    @Synchronized
    private fun recoverSwipeItem() {
        while (!removerQueue.isEmpty()) {
            val pos = removerQueue.poll().toInt()

            if (pos > -1) {
                recyclerView.adapter!!.notifyItemChanged(pos)
            }
        }
    }

    init {
        this.buttonList = ArrayList()
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.buttonBuffer = HashMap()
        this.removerQueue = IntLinkedList()

        attachSwipe()
    }

    fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    class IntLinkedList : LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }

        override fun add(element: Int): Boolean {
            return if (contains(element))
                false
            else super.add(element)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipePosition != pos) {
            removerQueue.add(swipePosition)
        }
        swipePosition = pos
        if (buttonBuffer.containsKey(swipePosition))
            buttonList = buttonBuffer[swipePosition]
        else
            buttonList!!.clear()
        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttonList!!.size.toFloat() * buttonWith.toFloat()
        recoverSwipeItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        var itemView = viewHolder.itemView

        if (pos < 0) {
            swipePosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<MyButton> = ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiaveMyButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size.toFloat() * buttonWith.toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive)
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: MutableList<MyButton>,
        pos: Int,
        translationX: Float,
    ) {
        var right = itemView.right.toFloat()
        var dButtonWidth = -1 * translationX / buffer.size

        for (button in buffer) {
            val left = right - dButtonWidth
            button.onFDraw(c,
                RectF(left, itemView.top.toFloat(), right - 10, itemView.bottom.toFloat()),
                pos)
            right = left
        }
    }
}
