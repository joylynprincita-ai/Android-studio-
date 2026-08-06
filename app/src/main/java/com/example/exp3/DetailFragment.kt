package com.example.exp3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailFragment : Fragment() {

    private var item: String? = null

    companion object {

        fun newInstance(item: String): DetailFragment {

            val fragment = DetailFragment()

            val bundle = Bundle()
            bundle.putString("item", item)

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = arguments?.getString("item")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val textView = view.findViewById<TextView>(R.id.txtDetails)

        textView.text = "Selected Item:\n\n$item"

        return view
    }
}