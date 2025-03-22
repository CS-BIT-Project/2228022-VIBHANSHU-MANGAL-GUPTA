package com.example.plan_your_day

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseDialogFragment : DialogFragment() {

    private lateinit var amountEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var calendarIcon: ImageView
    private lateinit var cancelButton: Button
    private lateinit var addButton: Button

    private var expenseAddListener: ExpenseAddListener? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    interface ExpenseAddListener {
        fun onExpenseAdded(transaction: ExpenseTransaction)
    }

    fun setExpenseAddListener(listener: ExpenseAddListener) {
        expenseAddListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        amountEditText = view.findViewById(R.id.amountEditText)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        dateEditText = view.findViewById(R.id.dateEditText)
        calendarIcon = view.findViewById(R.id.calendarIcon)
        cancelButton = view.findViewById(R.id.cancelButton)
        addButton = view.findViewById(R.id.addButton)

        setupCategorySpinner()
        setupDatePicker()
        setupButtonListeners()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Food & Dining", "Shopping", "Transportation", "Accommodation", "Entertainment")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = spinnerAdapter
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dateEditText.setText(dateFormat.format(calendar.time))

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateEditText.setText(dateFormat.format(calendar.time))
        }

        val showDatePicker = {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dateEditText.setOnClickListener { showDatePicker() }
        calendarIcon.setOnClickListener { showDatePicker() }
    }

    private fun setupButtonListeners() {
        cancelButton.setOnClickListener {
            dismiss()
        }

        addButton.setOnClickListener {
            addExpenseToFirestore()
        }
    }

    private fun addExpenseToFirestore() {
        val amountStr = amountEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val description = descriptionEditText.text.toString().trim()
        val dateStr = dateEditText.text.toString().trim()

        if (amountStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDouble()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateStr)
        val timestamp = date?.let { Timestamp(it) } ?: Timestamp.now()

        val transaction = ExpenseTransaction(
            amount = amount,
            category = category,
            description = description,
            timestamp = timestamp
        )

        val userUID = auth.currentUser?.uid
        if (userUID == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userUID)
            .collection("expenses")
            .add(transaction)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Expense added successfully!", Toast.LENGTH_SHORT).show()
                expenseAddListener?.onExpenseAdded(transaction)

                if (dialog?.isShowing == true) {
                    dismiss()
                } else {
                    parentFragmentManager.beginTransaction().remove(this).commit()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add expense", Toast.LENGTH_SHORT).show()
            }
    }
}
