package com.example.test2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max

class PlaceMapCanvas @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val places = mutableListOf<PlaceItem>()
    private val polylinePoints = mutableListOf<PlaceItem>()

    var onMapLongClickListener: ((Double, Double) -> Unit)? = null
    var onMarkerClickListener: ((PlaceItem) -> Unit)? = null

    private var selectedPlace: PlaceItem? = null

    // Zoom & Pan state
    private var scaleFactor = 1.0f
    private var focusX = 0f
    private var focusY = 0f

    // Rectangles for on-screen Zoom Buttons (+ / -)
    private val btnZoomInRect = RectF()
    private val btnZoomOutRect = RectF()

    // Paints
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8F0F8") // Soft map terrain color
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D0DCE5")
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(12f, 12f), 0f)
    }

    private val riverPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B3E5FC")
        strokeWidth = 40f
        style = Paint.Style.STROKE
    }

    private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 16f
        style = Paint.Style.STROKE
    }

    private val roadBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CFD8DC")
        strokeWidth = 22f
        style = Paint.Style.STROKE
    }

    private val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2979FF") // Bold route blue
        strokeWidth = 12f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val cardBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(10f, 0f, 4f, Color.argb(100, 0, 0, 0))
    }

    private val controlBtnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#37474F")
    }

    private val controlBtnTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 44f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.7f, 4.0f)
            invalidate()
            return true
        }
    })

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            // Check zoom buttons tap
            if (btnZoomInRect.contains(e.x, e.y)) {
                scaleFactor = (scaleFactor * 1.25f).coerceAtMost(4.0f)
                invalidate()
                return true
            }
            if (btnZoomOutRect.contains(e.x, e.y)) {
                scaleFactor = (scaleFactor / 1.25f).coerceAtLeast(0.7f)
                invalidate()
                return true
            }

            val tapped = findPlaceAt(e.x, e.y)
            if (tapped != null) {
                selectedPlace = tapped
                onMarkerClickListener?.invoke(tapped)
                invalidate()
                return true
            }
            selectedPlace = null
            invalidate()
            return super.onSingleTapConfirmed(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            focusX -= distanceX
            focusY -= distanceY
            invalidate()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val (lat, lng) = screenToLatLng(e.x, e.y)
            onMapLongClickListener?.invoke(lat, lng)
        }
    })

    fun setPlaces(newPlaces: List<PlaceItem>, route: List<PlaceItem> = emptyList()) {
        places.clear()
        places.addAll(newPlaces)
        polylinePoints.clear()
        polylinePoints.addAll(route)
        selectedPlace = null
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.save()
        canvas.translate(focusX, focusY)
        canvas.scale(scaleFactor, scaleFactor, w / 2, h / 2)

        // 1. Draw Map Background & River
        canvas.drawRect(-w, -h, w * 2, h * 2, bgPaint)

        // Draw River Path
        val riverPath = Path().apply {
            moveTo(-w * 0.2f, h * 0.4f)
            cubicTo(w * 0.3f, h * 0.1f, w * 0.7f, h * 0.9f, w * 1.2f, h * 0.6f)
        }
        canvas.drawPath(riverPath, riverPaint)

        // 2. Draw Decorative Map Grid & Roads
        for (i in -5..15) {
            val stepX = w / 10 * i
            val stepY = h / 10 * i
            canvas.drawLine(stepX, -h, stepX, h * 2, gridPaint)
            canvas.drawLine(-w, stepY, w * 2, stepY, gridPaint)
        }

        val roadPath = Path().apply {
            moveTo(-w * 0.1f, h * 0.25f)
            cubicTo(w * 0.25f, h * 0.2f, w * 0.75f, h * 0.8f, w * 1.1f, h * 0.75f)
            moveTo(w * 0.2f, -h * 0.1f)
            cubicTo(w * 0.45f, h * 0.5f, w * 0.55f, h * 0.5f, w * 0.8f, h * 1.1f)
        }
        canvas.drawPath(roadPath, roadBorderPaint)
        canvas.drawPath(roadPath, roadPaint)

        if (places.isEmpty()) {
            textPaint.color = Color.DKGRAY
            textPaint.textSize = 34f
            canvas.drawText("Sightseeing Map (Add places or tap 'Plan My Day')", w / 2, h / 2, textPaint)
            canvas.restore()
            drawZoomControls(canvas, w, h)
            return
        }

        // Bounding Box setup
        var minLat = places.minOf { it.latitude }
        var maxLat = places.maxOf { it.latitude }
        var minLng = places.minOf { it.longitude }
        var maxLng = places.maxOf { it.longitude }

        if (maxLat == minLat) { minLat -= 0.01; maxLat += 0.01 }
        if (maxLng == minLng) { minLng -= 0.01; maxLng += 0.01 }

        fun latToY(lat: Double): Float {
            val padding = 140f
            val norm = (lat - minLat) / (maxLat - minLat)
            return (h - padding) - (norm * (h - 2 * padding)).toFloat()
        }

        fun lngToX(lng: Double): Float {
            val padding = 140f
            val norm = (lng - minLng) / (maxLng - minLng)
            return padding + (norm * (w - 2 * padding)).toFloat()
        }

        // 3. Draw Polyline Route if available
        if (polylinePoints.size > 1) {
            val routePath = Path()
            val first = polylinePoints[0]
            routePath.moveTo(lngToX(first.longitude), latToY(first.latitude))

            for (i in 1 until polylinePoints.size) {
                val p = polylinePoints[i]
                routePath.lineTo(lngToX(p.longitude), latToY(p.latitude))
            }
            canvas.drawPath(routePath, routePaint)
        }

        // 4. Draw Markers
        for ((idx, place) in places.withIndex()) {
            val cx = lngToX(place.longitude)
            val cy = latToY(place.latitude)

            markerPaint.color = when {
                place.isFoodStop -> Color.parseColor("#FF9800") // Orange 🍔
                place.isRestStop -> Color.parseColor("#4CAF50") // Green ☕
                place.isDone -> Color.parseColor("#00ACC1")     // Azure ✓
                else -> Color.parseColor("#E53935")            // Red pin
            }

            // Outer pin circle
            canvas.drawCircle(cx, cy, 28f, markerPaint)

            // Inner white dot
            markerPaint.color = Color.WHITE
            canvas.drawCircle(cx, cy, 10f, markerPaint)

            // Label
            val badge = when {
                place.isFoodStop -> "🍔 ${place.name}"
                place.isRestStop -> "☕ ${place.name}"
                place.isDone -> "✓ ${place.name}"
                else -> "${idx + 1}. ${place.name}"
            }
            textPaint.color = Color.parseColor("#212121")
            textPaint.textSize = 30f
            canvas.drawText(badge, cx, cy - 38f, textPaint)
        }

        // 5. Draw Selected Marker Callout
        val sel = selectedPlace
        if (sel != null) {
            val cx = lngToX(sel.longitude)
            val cy = latToY(sel.latitude)

            val cardW = 340f
            val rect = RectF(cx - cardW / 2, cy - 160f, cx + cardW / 2, cy - 50f)
            canvas.drawRoundRect(rect, 16f, 16f, cardBgPaint)

            textPaint.color = Color.BLACK
            textPaint.textSize = 28f
            canvas.drawText(sel.name, cx, cy - 110f, textPaint)

            textPaint.color = Color.GRAY
            textPaint.textSize = 22f
            val subtitle = when {
                sel.isFoodStop -> "Food Stop 🍔"
                sel.isRestStop -> "Rest Stop ☕"
                sel.isDone -> "Completed ✓"
                else -> "Sightseeing Spot"
            }
            canvas.drawText(subtitle, cx, cy - 75f, textPaint)
        }

        canvas.restore()

        // 6. Draw Fixed On-Screen Zoom Controls (+ and -)
        drawZoomControls(canvas, w, h)
    }

    private fun drawZoomControls(canvas: Canvas, w: Float, h: Float) {
        val btnSize = 90f
        val margin = 30f

        btnZoomInRect.set(w - margin - btnSize, h - margin - (btnSize * 2 + 20f), w - margin, h - margin - (btnSize + 20f))
        btnZoomOutRect.set(w - margin - btnSize, h - margin - btnSize, w - margin, h - margin)

        canvas.drawRoundRect(btnZoomInRect, 16f, 16f, controlBtnPaint)
        canvas.drawText("+", btnZoomInRect.centerX(), btnZoomInRect.centerY() + 14f, controlBtnTextPaint)

        canvas.drawRoundRect(btnZoomOutRect, 16f, 16f, controlBtnPaint)
        canvas.drawText("-", btnZoomOutRect.centerX(), btnZoomOutRect.centerY() + 14f, controlBtnTextPaint)
    }

    private fun findPlaceAt(x: Float, y: Float): PlaceItem? {
        if (places.isEmpty()) return null
        val w = width.toFloat()
        val h = height.toFloat()

        var minLat = places.minOf { it.latitude }
        var maxLat = places.maxOf { it.latitude }
        var minLng = places.minOf { it.longitude }
        var maxLng = places.maxOf { it.longitude }

        if (maxLat == minLat) { minLat -= 0.01; maxLat += 0.01 }
        if (maxLng == minLng) { minLng -= 0.01; maxLng += 0.01 }

        fun latToY(lat: Double): Float {
            val padding = 140f
            val norm = (lat - minLat) / (maxLat - minLat)
            return (h - padding) - (norm * (h - 2 * padding)).toFloat()
        }

        fun lngToX(lng: Double): Float {
            val padding = 140f
            val norm = (lng - minLng) / (maxLng - minLng)
            return padding + (norm * (w - 2 * padding)).toFloat()
        }

        for (p in places) {
            val cx = lngToX(p.longitude)
            val cy = latToY(p.latitude)

            // Adjust touch coordinates for canvas scale & translation
            val mapX = (x - focusX - w / 2) / scaleFactor + w / 2
            val mapY = (y - focusY - h / 2) / scaleFactor + h / 2

            val dx = mapX - cx
            val dy = mapY - cy
            if (dx * dx + dy * dy <= 60 * 60) {
                return p
            }
        }
        return null
    }

    private fun screenToLatLng(x: Float, y: Float): Pair<Double, Double> {
        val w = max(1f, width.toFloat())
        val h = max(1f, height.toFloat())
        val baseLat = 48.8566
        val baseLng = 2.3522

        val mapX = (x - focusX - w / 2) / scaleFactor + w / 2
        val mapY = (y - focusY - h / 2) / scaleFactor + h / 2

        val lat = baseLat + ((h / 2 - mapY) / h) * 0.05
        val lng = baseLng + ((mapX - w / 2) / w) * 0.05
        return Pair(lat, lng)
    }
}
