package com.example.plan_your_day

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(private var transactions: List<ExpenseTransaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set category icon based on category
        val categoryIconRes = when (transaction.category) {
            "Food & Dining" -> R.drawable.ic_food
            "Shopping" -> R.drawable.ic_shopping
            "Transportation" -> R.drawable.ic_car
            "Accommodation" -> R.drawable.ic_bed
            "Entertainment" -> R.drawable.ic_ticket
            else -> R.drawable.ic_wallet
        }
        holder.categoryIcon.setImageResource(categoryIconRes)

        // Set text fields
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description
        holder.tvAmount.text = "₹${String.format("%.2f", transaction.amount)}"

        // Format timestamp to "12 Mar 2023, 07:30 PM" format
        val timestamp: Timestamp? = transaction.timestamp
        if (timestamp != null) {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            holder.tvDate.text = sdf.format(timestamp.toDate())
        } else {
            holder.tvDate.text = "N/A"
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    // Update data and refresh RecyclerView
    fun updateData(newTransactions: List<ExpenseTransaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
