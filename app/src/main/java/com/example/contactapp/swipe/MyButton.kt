package com.example.contactapp.swipe

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class MyButton(
    private val context: Context, private val text:String,
    private var textSize:Int,
    private var imageRecoursId:Int,
    private var color: Int,
    private var listener:MyButtonClickListener) {

    private var pos:Int = 0
    private var clickRegion:RectF? = null
    private var recources:Resources

    init {
        recources = context.resources
    }

    fun onClick(x:Float,y:Float):Boolean{
        if (clickRegion !=null&&clickRegion!!.contains(x,y)){
            listener.onclick(pos)
        }
        return false
    }

    fun onFDraw(c:Canvas,rectF: RectF,pos:Int){
        var p = Paint()
        p.color = color
        c.drawRect(rectF,p)

        p.color = Color.WHITE
        p.textSize = textSize.toFloat()

        val r = Rect()
        val cHeight =  rectF.height()
        val cWidth =  rectF.width()

        p.textAlign = Paint.Align.LEFT
        p.getTextBounds(text,0,text.length,r)

        var x = 0f
        var y = 0f

        if (imageRecoursId==0){
            x = cWidth/2f-r.width()/2f-r.left.toFloat()
            y = cHeight/2f+r.height()/2f-r.bottom.toFloat()
            c.drawText(text,rectF.left+x,rectF.top+y,p)
        }else{
            val d = ContextCompat.getDrawable(context,imageRecoursId)
            val bitmap = drawableToBitmap(d)
            c.drawBitmap(bitmap,(rectF.left+rectF.right)/2-25,(rectF.top+rectF.bottom)/2-20,p)
        }
        clickRegion = rectF
        this.pos = pos
    }
    private fun drawableToBitmap(d: Drawable?): Bitmap {
        if (d is BitmapDrawable) return d.bitmap
        val bitmap = Bitmap.createBitmap(d!!.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0,0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }
}