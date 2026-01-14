package com.example.group3bn

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

class CartFragment : Fragment(R.layout.fragment_cart) {

    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private var itemsTotal = 0.0
    private var shippingFee = 0.0
    private var finalTotal = 0.0

    private val selectedProducts = mutableListOf<Pair<Product, Int>>() // Pair<Product, quantity>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartLayout = view.findViewById<LinearLayout>(R.id.cartLayout)
        val tvItemsTotal = view.findViewById<TextView>(R.id.tvItemsTotal)
        val tvShipping = view.findViewById<TextView>(R.id.tvShipping)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
        val btnOrder = view.findViewById<Button>(R.id.btnPlaceOrder)

        val mainActivity = activity as MainActivity

        // Initial totals
        itemsTotal = 0.0
        shippingFee = 0.0
        finalTotal = 0.0
        selectedProducts.clear()

        tvItemsTotal.text = "Items: ₱0.00"
        tvShipping.text = "Shipping (15%): ₱0.00"
        tvTotal.text = "Total: ₱0.00"
        updateOrderButton(btnOrder, false)

        // Build cart items
        mainActivity.cartItems.forEach { entry ->
            val product = entry.key
            val quantity = entry.value

            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16.dp, 16.dp, 16.dp, 16.dp)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

// Product Image
            val imgProduct = ImageView(requireContext()).apply {
                setImageResource(product.imageRes)
                layoutParams = LinearLayout.LayoutParams(64.dp, 64.dp).apply {
                    marginEnd = 16.dp
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

// Text Container (vertical)
            val textLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvName = TextView(requireContext()).apply {
                text = if (quantity > 1) "${product.name} ($quantity)" else product.name
                textSize = 16f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            val tvPrice = TextView(requireContext()).apply {
                text = if (quantity > 1) {
                    "₱%.2f x %d = ₱%.2f".format(product.price, quantity, product.price * quantity)
                } else {
                    "₱%.2f".format(product.price)
                }
                textSize = 14f
                setTypeface(typeface, android.graphics.Typeface.ITALIC)
            }

            textLayout.addView(tvName)
            textLayout.addView(tvPrice)

// Checkbox
            val checkBox = CheckBox(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }
            }

// Assemble item layout
            itemLayout.addView(imgProduct)
            itemLayout.addView(textLayout)
            itemLayout.addView(checkBox)

// Add to parent cart layout
            cartLayout.addView(itemLayout)


            // Checkbox listener (same as before)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val itemAmount = product.price * quantity
                if (isChecked) {
                    itemsTotal += itemAmount
                    selectedProducts.add(Pair(product, quantity))
                } else {
                    itemsTotal -= itemAmount
                    selectedProducts.removeAll { it.first == product }
                }
                shippingFee = itemsTotal * 0.15
                finalTotal = itemsTotal + shippingFee

                tvItemsTotal.text = String.format("Items: ₱%.2f", itemsTotal)
                tvShipping.text = String.format("Shipping (15%%): ₱%.2f", shippingFee)
                tvTotal.text = String.format("Total: ₱%.2f", finalTotal)

                updateOrderButton(btnOrder, selectedProducts.isNotEmpty())
            }
        }


        // Place order
        // --- Place order button click ---
        btnOrder.setOnClickListener {
            // Don't remove items yet!
            // Pass total AND selected products to OrderFragment
            val bundle = Bundle().apply {
                putDouble("totalAmount", finalTotal)
                putSerializable("selectedItems", ArrayList(selectedProducts))
            }
            val orderFragment = OrderFragment()
            orderFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, orderFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateOrderButton(button: Button, hasSelection: Boolean) {
        if (hasSelection) {
            button.text = "Place order"
            button.isEnabled = true
            button.setBackgroundTintList(requireContext().getColorStateList(R.color.purple_500))
        } else {
            button.text = "Select item"
            button.isEnabled = false
            button.setBackgroundTintList(requireContext().getColorStateList(R.color.gray))
        }
    }
}
