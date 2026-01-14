package com.example.group3bn

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import kotlin.random.Random

class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var tvOrderSummary: TextView
    private lateinit var tvOrderTotal: TextView
    private lateinit var etAddress: EditText
    private lateinit var paymentSpinner: Spinner
    private lateinit var dynamicInputContainer: LinearLayout
    private lateinit var btnPlaceOrder: Button

    private lateinit var mainActivity: MainActivity
    private var totalAmount = 0.0
    private val purchasedItems = mutableListOf<Pair<Product, Int>>() // from CartFragment

    private var orderPlaced = false

    override fun onDestroyView() {
        super.onDestroyView()
        if (!orderPlaced) {
            purchasedItems.forEach { (p, qty) ->
                val currentQty = mainActivity.cartItems[p] ?: 0
                mainActivity.cartItems[p] = currentQty + qty
            }
            mainActivity.updateCartBadge()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mainActivity = activity as MainActivity

        tvOrderSummary = view.findViewById(R.id.tvOrderSummary)
        tvOrderTotal = view.findViewById(R.id.tvOrderTotal)
        etAddress = view.findViewById(R.id.etAddress)
        paymentSpinner = view.findViewById(R.id.paymentSpinner)
        dynamicInputContainer = view.findViewById(R.id.dynamicInputContainer)
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder)

        // Get total and items from arguments
        totalAmount = arguments?.getDouble("totalAmount") ?: 0.0
        val items = arguments?.getSerializable("selectedItems") as? List<Pair<Product, Int>>
        items?.let { purchasedItems.addAll(it) }

        // Build summary text
        val summaryText = purchasedItems.joinToString("\n") { (p, qty) ->
            if (qty > 1) "${p.name} ($qty) - ₱%.2f x %d = ₱%.2f".format(p.price, qty, p.price * qty)
            else "${p.name} - ₱%.2f".format(p.price)
        }
        tvOrderSummary.text = summaryText
        tvOrderTotal.text = "Total: ₱%.2f".format(totalAmount)

        val paymentOptions = listOf(
            "Cash on Delivery",
            "G-Cash",
            "Credit / Debit Card",
            "Pay Maya"
        )

        paymentSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, paymentOptions)

        val inputFields = mutableListOf<EditText>() // to validate
        paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selected = paymentOptions[position]
                dynamicInputContainer.removeAllViews()
                inputFields.clear()

                when (selected) {
                    "Cash on Delivery" -> {}
                    "G-Cash" -> {
                        val et = createEditText("Philippine mobile number", InputType.TYPE_CLASS_PHONE)
                        dynamicInputContainer.addView(et)
                        inputFields.add(et)
                    }
                    "Credit / Debit Card" -> {
                        val etName = createEditText("Full Name on Card", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        val etCard = createEditText("Card Number", InputType.TYPE_CLASS_NUMBER)
                        val expiryCvvLayout = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }
                        val etExpiry = createEditText("MM/YY", InputType.TYPE_CLASS_DATETIME).apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 8 } }
                        val etCvv = createEditText("CVV", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD).apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
                        expiryCvvLayout.addView(etExpiry)
                        expiryCvvLayout.addView(etCvv)
                        val etBilling = createEditText("Billing Address", InputType.TYPE_CLASS_TEXT)
                        val etZip = createEditText("ZIP Code", InputType.TYPE_CLASS_NUMBER)

                        dynamicInputContainer.addView(etName)
                        dynamicInputContainer.addView(etCard)
                        dynamicInputContainer.addView(expiryCvvLayout)
                        dynamicInputContainer.addView(etBilling)
                        dynamicInputContainer.addView(etZip)

                        inputFields.addAll(listOf(etName, etCard, etExpiry, etCvv, etBilling, etZip))
                    }
                    "Pay Maya" -> {
                        val et = createEditText("Registered Maya number", InputType.TYPE_CLASS_PHONE)
                        dynamicInputContainer.addView(et)
                        inputFields.add(et)
                    }
                }

                // Enable place order only if address + payment fields filled
                etAddress.addTextChangedListener { checkEnablePlaceOrder(inputFields) }
                inputFields.forEach { it.addTextChangedListener { checkEnablePlaceOrder(inputFields) } }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnPlaceOrder.setOnClickListener {
            orderPlaced = true
            // Remove items from cart
            purchasedItems.forEach { (p, _) -> mainActivity.cartItems.remove(p) }
            mainActivity.updateCartBadge()

            // Show success
            val deliveryDays = Random.nextInt(3, 8)
            Toast.makeText(requireContext(), "Order placed! Delivery in $deliveryDays days.", Toast.LENGTH_LONG).show()

            // Go to HomeFragment
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun checkEnablePlaceOrder(inputFields: List<EditText>) {
        val allFilled = etAddress.text.isNotBlank() && inputFields.all { it.text.isNotBlank() }
        btnPlaceOrder.isEnabled = allFilled
        btnPlaceOrder.backgroundTintList = requireContext().getColorStateList(
            if (allFilled) R.color.purple_500 else R.color.gray
        )
    }

    private fun createEditText(hint: String, inputType: Int): EditText {
        return EditText(requireContext()).apply {
            this.hint = hint
            this.inputType = inputType
            setPadding(16, 16, 16, 16)
            background = requireContext().getDrawable(R.drawable.rounded_edittext)
        }
    }
}
