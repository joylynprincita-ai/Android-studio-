package com.example.exp3

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

class ListFragment : Fragment() {

    private lateinit var listener: OnItemSelectedListener

    private val items = arrayOf(
        "Android",
        "Java",
        "Python",
        "Kotlin",
        "Flutter"
    )

    interface OnItemSelectedListener {
        fun onItemSelected(item: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnItemSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById<ListView>(R.id.listView)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            items
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            listener.onItemSelected(items[position])
        }

        return view
    }
}