package com.example.group3bn

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // Product -> Quantity
    val cartItems = mutableMapOf<Product, Int>()

    lateinit var bottomNav: BottomNavigationView
    private lateinit var cartBadge: BadgeDrawable

    // Single Toast (anti-spam)
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)

        // Load Home by default
        loadFragment(HomeFragment())

        // Setup badge
        cartBadge = bottomNav.getOrCreateBadge(R.id.nav_cart)
        updateCartBadge()

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_cart -> loadFragment(CartFragment())
                R.id.nav_order -> loadFragment(OrderFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ✅ ADD TO CART (handles duplicates)
    fun addToCart(product: Product) {
        val currentQty = cartItems[product] ?: 0
        cartItems[product] = currentQty + 1

        // Spam-safe toast
        toast?.cancel()
        toast = Toast.makeText(
            this,
            "${product.name} added to cart.",
            Toast.LENGTH_SHORT
        )
        toast?.show()

        updateCartBadge()
    }

    // ✅ REMOVE PRODUCTS AFTER ORDER
    fun removeFromCart(product: Product) {
        cartItems.remove(product)
        updateCartBadge()
    }

    // ✅ UPDATE BADGE COUNT
    fun updateCartBadge() {
        val totalItems = cartItems.values.sum()

        cartBadge.isVisible = totalItems > 0
        cartBadge.number = totalItems
    }
}
