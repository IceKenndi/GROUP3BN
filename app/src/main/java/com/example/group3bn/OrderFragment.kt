package com.example.group3bn

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment

class  OrderFragment : Fragment(R.layout.fragment_order) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = view.findViewById<Spinner>(R.id.paymentSpinner)

        val payments = arrayOf("Cash", "GCash", "PayMaya")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            payments
        )

        spinner.adapter = adapter
    }
}
