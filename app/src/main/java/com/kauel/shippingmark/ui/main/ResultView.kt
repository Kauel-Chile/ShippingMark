// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
package com.kauel.shippingmark.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ResultView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mPaintRectangle: Paint? = null
    private var mPaintText: Paint? = null
    private var mResults: ArrayList<Result>? = null

    init {
        mPaintRectangle = Paint()
        mPaintText = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mResults == null) return
        for (result in mResults!!) {
            mPaintRectangle!!.strokeWidth = 5f
            mPaintRectangle!!.style = Paint.Style.STROKE
            when (result.classIndex) {
                1 -> {mPaintRectangle!!.color = Color.GREEN}
                2 -> {mPaintRectangle!!.color = Color.RED}
                3 -> {mPaintRectangle!!.color = Color.YELLOW}
            }
            canvas.drawRect(result.rect, mPaintRectangle!!)
        }
    }

    fun setResults(results: ArrayList<Result>?) {
        mResults = results
    }
}