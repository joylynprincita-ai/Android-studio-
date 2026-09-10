package com.example.test

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class SupportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_support,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.tvSupportTitle)
        val description =
            view.findViewById<TextView>(R.id.tvSupportDescription)

        val studentName =
            view.findViewById<EditText>(R.id.etStudentName)

        val problem =
            view.findViewById<EditText>(R.id.etProblem)

        val submit =
            view.findViewById<Button>(R.id.btnSubmit)

        val returnHome =
            view.findViewById<Button>(R.id.btnReturnHome)

        val category =
            arguments?.getString("category") ?: "Support"

        // Display selected category
        title.text = category

        // Display category description
        description.text = when (category) {
            "Academic Support" ->
                "Get help with your academic concerns."

            "Technical Support" ->
                "Get help with technical problems."

            "Library Support" ->
                "Get help with library-related concerns."

            else ->
                "Get student support."
        }

        // Submit Request
        submit.setOnClickListener {

            val name = studentName.text.toString().trim()

            if (name.isEmpty()) {
                studentName.error = "Please enter your name"
                return@setOnClickListener
            }

            val intent = Intent(
                requireContext(),
                RequestConfirmationActivity::class.java
            )

            intent.putExtra("studentName", name)
            intent.putExtra("category", category)

            startActivity(intent)
        }

        // Return Home
        returnHome.setOnClickListener {

            requireActivity()
                .supportFragmentManager
                .popBackStack()
        }
    }

    companion object {

        fun newInstance(category: String): SupportFragment {

            val fragment = SupportFragment()

            val bundle = Bundle()
            bundle.putString("category", category)

            fragment.arguments = bundle

            return fragment
        }
    }
}