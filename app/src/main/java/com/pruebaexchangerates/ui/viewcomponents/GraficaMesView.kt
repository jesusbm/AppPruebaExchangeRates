package com.pruebaexchangerates.ui.viewcomponents

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.marginStart
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.pruebaexchangerates.R

fun View.widthF(): Float {
    return width.toFloat() - paddingStart - paddingEnd
}

fun View.heightF(): Float {
    return height.toFloat() - paddingBottom
}

class GraficaMesView(context: Context, attrs: AttributeSet?) : View(context, attrs),
    View.OnTouchListener {

    /**
     * Sirve para notificar a la vista contenedora si este componente esta haciendo scroll
     * */
    interface OnTouchStateChangeListener {
        fun onHorizontalDragStarted()
        fun onHorizontalDragEnded()
    }

    private val TAG = GraficaMesView::class.java.simpleName

    var onTouchStateChangeListener: OnTouchStateChangeListener? = null
        set
        get

    fun setData(dataX: List<Int>, dataY: List<Double>, dataLabel: List<Double>) {
        if (dataX.isNotEmpty() && dataX.size == dataY.size) {
            val maximum = dataY.max()!!
            val minimum = dataY.min()!!
            this.datosX = dataX
            this.datosY = dataY.map { it - minimum }.map { 10 + it * 80 / (maximum - minimum) }
            //this.datosY = dataY.map { it * (100 - 10) / (maximum - minimum) + 10 }
            this.datosLabel = dataLabel
        }
    }

    private var datosX = (1..30).toList()
    private var datosY = datosX.map { 0 * 100.0 }
    private var datosLabel = datosX.map { 0 * 100.0 }
    val datosInfo =
        datosY.mapIndexed { index, value -> "$value visita${if (index > 1) "s" else ""} en el dia $index" }

    private val boxStrokeWidth = 8f

    private val textColor = Color.WHITE
    private var textHeight = 50f
    private val textBoundsRect = Rect()

    private var textLabelHeight = 30f
    private val textLabelBoundsRect = Rect()

    private var reducingFactor = 0.65f
    private val indicatorRadius: Float = 20f
    private var isPressedView: Boolean = false
    private var motionEventView: MotionEvent? = null

    var barScaleAnimation: Float = 0f

    private val proportionVisible: Int = 4
    private val proportionInvisible: Int = 2
    private val widthPart: Float by lazy { widthF() / datosX.size }
    private val widthSubPart: Float by lazy { widthPart / (proportionVisible + proportionInvisible) }
    private val widthVisiblePart: Float by lazy { widthSubPart * proportionVisible }
    private val widthInvisiblePart: Float by lazy { widthSubPart * proportionInvisible }
    private val offsetStart: Float by lazy { (widthInvisiblePart + widthVisiblePart) / 2 }
    private val scaleHeight: Float by lazy { reducingFactor * ((heightF() - widthVisiblePart) / 100) }

    init {
        setOnTouchListener(this)
    }

    /**
     *
     * */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {

            //dibujamos la lineas de 50 y 100 %
            val y50 = heightF() - barScaleAnimation * 50 * scaleHeight - widthVisiblePart / 2
            val y100 = heightF() - barScaleAnimation * 100 * scaleHeight - widthVisiblePart / 2
            drawLine(0f, y50, widthF() + paddingStart + paddingEnd, y50, boxStrokePaint)
            drawLine(0f, y100, widthF() + paddingStart + paddingEnd, y100, boxStrokePaint)

            //dibujamos las barras
            datosY.forEachIndexed { index, value ->
                if (0 < value) {
                    val startX = widthPart * index + offsetStart + paddingStart
                    val stopX = startX
                    val startY = heightF() - barScaleAnimation * value * scaleHeight
                    val stopY = heightF() - boxStrokeWidth
                    //Log.e(TAG, "VALORES: ${datosY}")
                    drawLine(startX, startY.toFloat(), stopX, stopY, plotStrokePaint)
                }
            }

            //dibujamos la linea base
            drawRect(
                0f + paddingStart,
                heightF(),
                widthF() + paddingStart,
                heightF() + widthVisiblePart,
                boxFillPaint
            )
            drawLine(
                0f,
                heightF() - boxStrokeWidth / 2,
                widthF() + paddingStart + paddingEnd,
                heightF() - boxStrokeWidth / 2,
                boxStrokePaint
            )

            //dibujamos el marcador y la info de la barra seleccionada
            if (isPressedView) {
                motionEventView?.let {
                    val Xlocal = it.x - marginStart
                    var indiceContenedor: Int? = null
                    var Xover: Float? = null

                    //encontramos el valor de X que el usuario esta presionando y el intervalo al que pertenece en el eje X
                    datosX.forEachIndexed { index, value ->
                        val startX = widthPart * index + offsetStart + paddingStart
                        if (null == indiceContenedor && Xlocal >= startX && Xlocal <= startX + widthPart) {
                            indiceContenedor = index
                            Xover = widthPart * index + offsetStart + paddingStart
                        }
                    }

                    indiceContenedor?.let { contenedor ->
                        Xover?.let { Xover ->

                            val textTopPosition = 10f

                            //dibujamos la info de la barra seleccionada
                            val valorAuxX = datosX[contenedor]
                            val valorAuxY = (datosLabel[contenedor] * 1000000).toInt() / 1000000.0
                            val text = "Dia $valorAuxX : 1 EUR = $valorAuxY USD"
                            val textStartPositionX = calculateTextStartPosition(
                                text,
                                Xover,
                                0,
                                widthF() + paddingStart + paddingEnd
                            )

                            //dibujamos el marco alrededor de la info
                            val (textBorderPath, marcadorPosX, marcadorPosY) = calculateTextBorderPath(
                                text,
                                Xover,
                                textStartPositionX,
                                textTopPosition
                            )
                            drawPath(textBorderPath, textBorderPaintLine)
                            drawText(text, textStartPositionX, textHeight, textPaint)

                            //dibujamos el marcador
                            val yc =
                                (heightF() - barScaleAnimation * datosY[contenedor] * scaleHeight).toFloat()
                            drawLine(Xover, yc, marcadorPosX, marcadorPosY, indicatorPaintLine)
                            drawCircle(Xover, yc, indicatorRadius, indicatorBackgroundStrokePaint)
                            drawCircle(Xover, yc, indicatorRadius / 2, indicatorPaintRing)
                            drawCircle(Xover, yc, indicatorRadius / 4, indicatorPaintCenter)
                        }
                    }
                }
            }

            //dibujamos las etiquetas del eje horizontal
            datosX.forEachIndexed { index, valueX ->
                if (0 == valueX % 5 || 0 == index || datosX.size == index) {
                    val text = "${valueX}"
                    textLabelPaint.getTextBounds(text, 0, text.length, textLabelBoundsRect)
                    val startX =
                        widthPart * index + offsetStart + paddingStart - textLabelBoundsRect.centerX()
                    val startY =
                        heightF() + 1.5f * (textLabelBoundsRect.bottom - textLabelBoundsRect.top)
                    drawText(text, startX, startY, textLabelPaint)
                }
            }
        }
    }

    fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            startDelay = 50
            duration = 450
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { valueAnimator ->
                barScaleAnimation = valueAnimator.animatedValue as Float
                invalidate()
            }
        }.start()
    }

    private val boxStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = boxStrokeWidth
        color = context.getColor(R.color.color_textcolor_discreto)
    }

    private val boxFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.color_background_fragment)
    }

    private val plotStrokePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = widthVisiblePart
            color = context.getColor(R.color.color_textcolor_normal)//Color.BLUE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val indicatorPaintCenter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private val indicatorPaintRing = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    private val indicatorBackgroundStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.azul_translucido)
    }

    private val indicatorPaintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
        color = context.getColor(R.color.color_textcolor_discreto)
    }

    private val textBorderPaintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 2f
        pathEffect = CornerPathEffect(5f)
        color = context.getColor(R.color.color_textcolor_normal)
    }

    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.color_background_fragment)//textColor
        if (textHeight == 0f) {
            textHeight = textSize
        } else {
            textSize = textHeight
        }
    }

    private val textLabelPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.color_textcolor_discreto)
        textSize = textLabelHeight
    }

    private val shadowPaint = Paint(0).apply {
        color = 0x101010
        maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
    }

    private fun getEventName(event: MotionEvent?): String {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            else -> "ELSE: ${event}"
        }
    }


    /**
     * View.OnTouchListener
     * */
    override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
        Log.e(TAG, "${getEventName(motionEvent)}: ${isPressedView}")
        motionEventView = motionEvent
        val isMotionEventIsInsideView = checkIfMotionEventIsInsideView(motionEvent)
        val isPressedViewUpdate = if (isMotionEventIsInsideView) {
            Log.e(TAG, "IF: ---")
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    view?.parent?.requestDisallowInterceptTouchEvent(true);
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> false
                else -> false
            }
        } else {
            Log.e(TAG, "ELSE: ---")
            false
        }

        Log.e(TAG, "isPressedViewUpdate: ${isPressedViewUpdate}, isPressedView: ${isPressedView}")
        if (isPressedViewUpdate != isPressedView) {
            isPressedView = isPressedViewUpdate
            invalidate()
            if (isPressedView) onTouchStateChangeListener?.onHorizontalDragStarted() else onTouchStateChangeListener?.onHorizontalDragEnded()
        }
        if (isPressedView) {
            invalidate()
        }
        return isMotionEventIsInsideView
    }

    private fun checkIfMotionEventIsInsideView(motionEvent: MotionEvent): Boolean {
        val rectView = Rect()
        this.getGlobalVisibleRect(rectView)
        return rectView.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
    }


    private fun calculateTextStartPosition(
        text: String,
        Xover: Float,
        infLimit: Int,
        supLimit: Float
    ): Float {
        val marginHorizontal = 35f
        textPaint.getTextBounds(text, 0, text.length, textBoundsRect)
        val textHalf = textBoundsRect.centerX()
        return if (infLimit + marginHorizontal > Xover - textHalf) {
            0f + marginHorizontal
        } else {
            if (Xover + textHalf > supLimit - marginHorizontal) {
                supLimit - 2 * textHalf - marginHorizontal
            } else {
                Xover - textHalf
            }
        }
    }


    private fun calculateTextBorderPath(
        text: String,
        xover: Float,
        textStartPositionX: Float,
        textTopPosition: Float
    ): Triple<Path, Float, Float> {
        val textEndPositionX = textStartPositionX + textBoundsRect.right - textBoundsRect.left
        val textBottomPosition = textTopPosition + textHeight
        textPaint.getTextBounds(text, 0, text.length, textBoundsRect)
        val correction = 30f
        val anchoMarcador = 20f
        val alturaMarcador = 30f
        val path = Path()
        path.moveTo(xover, textBottomPosition + alturaMarcador)
        path.lineTo(xover - anchoMarcador, textBottomPosition)
        path.lineTo(textStartPositionX, textBottomPosition)
        path.arcTo(
            textStartPositionX - correction,
            textTopPosition,
            textStartPositionX,
            textBottomPosition,
            90f,
            180f,
            false
        )
        path.lineTo(textEndPositionX, textTopPosition)
        path.arcTo(
            textEndPositionX,
            textTopPosition,
            textEndPositionX + correction,
            textBottomPosition,
            270f,
            180f,
            false
        )
        path.lineTo(xover + anchoMarcador, textBottomPosition)
        path.close()
        return Triple(path, xover, textBottomPosition + alturaMarcador)
    }
}
