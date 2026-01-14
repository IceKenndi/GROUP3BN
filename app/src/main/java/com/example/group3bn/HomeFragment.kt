package com.example.group3bn

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.productRecycler)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        // Step 1: create a list of Product objects
        val products = listOf(
            Product("T-Shirt", R.drawable.tshirt_image, 299.00),
            Product("Jeans", R.drawable.jeans_image, 299.00),
            Product("Hat", R.drawable.hat_image, 299.00),
            Product("Shoes", R.drawable.shoes_image, 299.00),
            Product("Jacket", R.drawable.jacket_image, 299.00),
            Product("Socks", R.drawable.socks_image, 299.00),
            Product("Sweater", R.drawable.sweater_image, 299.00),
            Product("Cap", R.drawable.cap_image, 299.00),
            Product("Scarf", R.drawable.scarf_image, 299.00),
            Product("Gloves", R.drawable.gloves_image, 299.00)
        )

        // Step 2: pass the product list to the adapter
        recycler.adapter = ProductAdapter(products) { product ->
            // Step 3: call MainActivity's goToCart with the Product object
            (activity as? MainActivity)?.addToCart(product)
        }
    }
}
