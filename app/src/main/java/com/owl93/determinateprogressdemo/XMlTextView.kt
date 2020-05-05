package com.owl93.determinateprogressdemo

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.xmlStyle(size: Float, color: Int, xmlText: String) {
    textSize = size
    setTextColor(color)
    typeface = Typeface.MONOSPACE
    text = xmlText
}

class XMlTextView(context: Context, 
                  val textSize: Float, 
                  val blockName: String,
                  val nameSpaces: Array<String>, 
                  val attributes: Array<String>, 
                  val values: Array<String>, 
                  var colors: IntArray? = null
): LinearLayout(context) {
    private val rowParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    init {
        if(values.size != attributes.size)
            throw IllegalArgumentException("Arrays passed to constructor are not of equal size")
        if(values.size != nameSpaces.size)
            throw IllegalArgumentException("Arrays passed to constructor are not of equal size")
        orientation = VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        gravity = Gravity.START
        if(colors == null) {
            colors = intArrayOf(
                ContextCompat.getColor(context, R.color.colorOnSurface),
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context,R.color.purple200)
            )
        }
        addView(
            LinearLayout(context).also {
                it.orientation = HORIZONTAL
                it.layoutParams = rowParams
                it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![0], "<$blockName")})
            }
        )
        for (i in attributes.indices) addView(buildRow(i))

        addView(LinearLayout(context).also {
            it.orientation = HORIZONTAL
            it.layoutParams = rowParams
            it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![0],"/>") })
        })

    }
    
    private fun buildRow(idx: Int): LinearLayout {
        return LinearLayout(context).also {
            it.orientation = HORIZONTAL
            it.layoutParams = rowParams
            it.setPadding(72, 0, 0, 0)
            it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![1], "${nameSpaces[idx]}:") })
            it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![2], attributes[idx]) })
            it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![0], "=\"")})
            it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![3], values[idx]) })
            it.addView(TextView(context).also { tv -> tv.xmlStyle(textSize, colors!![0], "\"") })
        }
    }

    fun getText() : String {
        return StringBuilder("<$blockName\n").apply {
            for(i in attributes.indices) {
                append("\t${nameSpaces[i]}:${attributes[i]}=\"${values[i]}\"\n")
            }
            append("/>")
        }.toString()
    }
    
}