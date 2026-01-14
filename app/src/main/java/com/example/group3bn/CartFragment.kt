package com.example.group3bn

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class CartFragment : Fragment(R.layout.fragment_cart) {

    private var selectedTotal = 0.0
    private val selectedProducts = mutableListOf<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartLayout = view.findViewById<LinearLayout>(R.id.cartLayout)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
        val btnOrder = view.findViewById<Button>(R.id.btnPlaceOrder)

        val mainActivity = activity as? MainActivity
        selectedTotal = 0.0
        selectedProducts.clear()

        mainActivity?.cartItems?.forEach { product ->
            // Horizontal layout for each item
            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(8, 8, 8, 8)
            }

            val checkBox = CheckBox(requireContext())
            val tvItem = TextView(requireContext()).apply {
                text = "${product.name} - ₱${product.price}"
                textSize = 16f
                setPadding(8, 0, 0, 0)
            }

            // Checkbox for selecting products
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTotal += product.price
                    selectedProducts.add(product)
                } else {
                    selectedTotal -= product.price
                    selectedProducts.remove(product)
                }
                tvTotal.text = "Total: ₱%.2f".format(selectedTotal)
                btnOrder.visibility = if (selectedProducts.isNotEmpty()) View.VISIBLE else View.GONE
            }

            itemLayout.addView(checkBox)
            itemLayout.addView(tvItem)

            cartLayout.addView(itemLayout)
        }

        tvTotal.text = "Total: ₱%.2f".format(selectedTotal)
        btnOrder.visibility = View.GONE // hide button until something is selected

        btnOrder.setOnClickListener {
            // Remove selected products from cart
            selectedProducts.forEach { mainActivity?.cartItems?.remove(it) }

            // Reload cart fragment to reflect removal
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .commit()
        }
    }
}
