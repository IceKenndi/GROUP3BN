package com.example.group3bn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onAddClick: (Product) -> Unit // only called when Add button is clicked
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvProductName)
        private val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        private val btnAdd: ImageButton = view.findViewById(R.id.btnAdd)

        fun bind(product: Product) {
            tvName.text = product.name
            imgProduct.setImageResource(product.imageRes)

            // Optional: price TextView
            val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
            tvPrice.text = "₱%.2f".format(product.price)

            // Only Add button triggers cart
            btnAdd.setOnClickListener {
                onAddClick(product) // calls MainActivity.addToCart
            }

            // Parent click does nothing
            view.setOnClickListener { /* do nothing */ }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}
