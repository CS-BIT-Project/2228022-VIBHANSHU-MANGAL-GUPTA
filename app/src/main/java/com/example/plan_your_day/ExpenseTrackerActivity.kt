package com.example.plan_your_day

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.NumberFormat
import java.util.Locale

class ExpenseTrackerActivity : AppCompatActivity(), AddExpenseDialogFragment.ExpenseAddListener {

    // UI Elements
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvFoodAmount: TextView
    private lateinit var tvShoppingAmount: TextView
    private lateinit var tvTransportAmount: TextView
    private lateinit var tvAccommodationAmount: TextView
    private lateinit var tvEntertainmentAmount: TextView
    private lateinit var recentTransactionsRecyclerView: RecyclerView
    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var btnBack: ImageButton

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Adapter and list
    private val transactionsList = mutableListOf<ExpenseTransaction>()
    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_tracker)

        initViews()
        setupRecyclerView()
        loadExpensesFromFirestore()
        setupClickListeners()
    }

    private fun initViews() {
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvFoodAmount = findViewById(R.id.tvFoodAmount)
        tvShoppingAmount = findViewById(R.id.tvShoppingAmount)
        tvTransportAmount = findViewById(R.id.tvTransportAmount)
        tvAccommodationAmount = findViewById(R.id.tvAccommodationAmount)
        tvEntertainmentAmount = findViewById(R.id.tvEntertainmentAmount)
        recentTransactionsRecyclerView = findViewById(R.id.recentTransactionsRecyclerView)
        fabAddExpense = findViewById(R.id.fabAddExpense)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        transactionsAdapter = TransactionsAdapter(transactionsList)
        recentTransactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ExpenseTrackerActivity)
            adapter = transactionsAdapter
        }
    }

    private fun loadExpensesFromFirestore() {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        if (userUID == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userUID) // 🔹 Fetch expenses only for logged-in user
            .collection("expenses")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                transactionsList.clear()
                snapshots?.forEach { document ->
                    val transaction = document.toObject(ExpenseTransaction::class.java)
                    transactionsList.add(transaction)
                }
                transactionsAdapter.notifyDataSetChanged()
                updateExpenseTotals()
            }
    }


    private fun updateExpenseTotals() {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        if (userUID == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userUID).collection("expenses")
            .get()
            .addOnSuccessListener { snapshots ->
                var totalExpenses = 0.0
                val categoryTotals = mutableMapOf(
                    "Food & Dining" to 0.0,
                    "Shopping" to 0.0,
                    "Transportation" to 0.0,
                    "Accommodation" to 0.0,
                    "Entertainment" to 0.0
                )

                snapshots.forEach { document ->
                    val amount = document.getDouble("amount") ?: 0.0
                    val category = document.getString("category") ?: "Other"
                    totalExpenses += amount
                    categoryTotals[category] = categoryTotals.getOrDefault(category, 0.0) + amount
                }

                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

                tvTotalExpenses.text = currencyFormat.format(totalExpenses).replace("INR", "₹")
                tvFoodAmount.text = currencyFormat.format(categoryTotals["Food & Dining"] ?: 0.0).replace("INR", "₹")
                tvShoppingAmount.text = currencyFormat.format(categoryTotals["Shopping"] ?: 0.0).replace("INR", "₹")
                tvTransportAmount.text = currencyFormat.format(categoryTotals["Transportation"] ?: 0.0).replace("INR", "₹")
                tvAccommodationAmount.text = currencyFormat.format(categoryTotals["Accommodation"] ?: 0.0).replace("INR", "₹")
                tvEntertainmentAmount.text = currencyFormat.format(categoryTotals["Entertainment"] ?: 0.0).replace("INR", "₹")
            }
    }


    private fun setupClickListeners() {
        fabAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showAddExpenseDialog() {
        val dialogFragment = AddExpenseDialogFragment()
        dialogFragment.setExpenseAddListener(this)
        dialogFragment.show(supportFragmentManager, "AddExpenseDialog")
    }

    override fun onExpenseAdded(transaction: ExpenseTransaction) {
        updateExpenseTotals() // Just update totals
    }


}
