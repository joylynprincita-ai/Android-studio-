package com.example.test2

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

class PlaceAdapter(
    context: Context,
    private val places: MutableList<PlaceItem>,
    private val onItemChecked: (PlaceItem, Boolean) -> Unit,
    private val onItemDeleted: (PlaceItem) -> Unit
) : ArrayAdapter<PlaceItem>(context, 0, places) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_place,
            parent,
            false
        )

        val place = getItem(position) ?: return view

        val cbDone = view.findViewById<CheckBox>(R.id.cbDone)
        val tvPlaceName = view.findViewById<TextView>(R.id.tvPlaceName)
        val tvPlaceBadge = view.findViewById<TextView>(R.id.tvPlaceBadge)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvPlaceName.text = place.name

        // Strikethrough if done
        if (place.isDone) {
            tvPlaceName.paintFlags = tvPlaceName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            tvPlaceName.setTypeface(null, Typeface.ITALIC)
        } else {
            tvPlaceName.paintFlags = tvPlaceName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            tvPlaceName.setTypeface(null, Typeface.BOLD)
        }

        cbDone.isChecked = place.isDone

        cbDone.setOnCheckedChangeListener(null)
        cbDone.setOnCheckedChangeListener { _, isChecked ->
            place.isDone = isChecked
            onItemChecked(place, isChecked)
            notifyDataSetChanged()
        }

        // Set badge tag
        when {
            place.isFoodStop -> {
                tvPlaceBadge.text = "Food Stop 🍔"
                tvPlaceBadge.setTextColor(context.getColor(android.R.color.holo_orange_dark))
            }
            place.isRestStop -> {
                tvPlaceBadge.text = "Rest Stop ☕"
                tvPlaceBadge.setTextColor(context.getColor(android.R.color.holo_green_dark))
            }
            else -> {
                tvPlaceBadge.text = String.format("Lat: %.4f, Lng: %.4f", place.latitude, place.longitude)
                tvPlaceBadge.setTextColor(context.getColor(android.R.color.darker_gray))
            }
        }

        btnDelete.setOnClickListener {
            onItemDeleted(place)
        }

        return view
    }
}
