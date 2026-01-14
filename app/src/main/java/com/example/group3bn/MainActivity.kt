package com.example.group3bn

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.badge.BadgeDrawable

class MainActivity : AppCompatActivity() {

    val cartItems = mutableListOf<Product>()
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var cartBadge: BadgeDrawable

    // Single Toast object to avoid multiple stacked toasts
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)

        // Load home screen by default
        loadFragment(HomeFragment())

        // Setup cart badge
        cartBadge = bottomNav.getOrCreateBadge(R.id.nav_cart)
        cartBadge.isVisible = cartItems.isNotEmpty()
        cartBadge.number = cartItems.size

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

    // Adds product to cart with dynamic badge and single toast
    fun addToCart(product: Product) {
        cartItems.add(product)

        // Show a single toast, cancel previous if it exists
        toast?.cancel()
        toast = Toast.makeText(this, "${product.name} added to cart.", Toast.LENGTH_SHORT)
        toast?.show()

        // Update badge dynamically
        cartBadge.isVisible = cartItems.isNotEmpty()
        cartBadge.number = cartItems.size
    }
}
