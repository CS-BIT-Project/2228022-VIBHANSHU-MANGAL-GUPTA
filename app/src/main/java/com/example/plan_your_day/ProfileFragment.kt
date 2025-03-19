package com.example.plan_your_day

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.plan_your_day.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user: FirebaseUser? = firebaseAuth.currentUser

        if (user != null) {
            binding.textViewUserEmail.text = user.email ?: "No Email"
            fetchUserData(user.uid)
        } else {
            binding.textViewUserName.text = "User not logged in"
            binding.textViewUserEmail.text = ""
            binding.profileImage.setImageResource(R.drawable.default_profile) // Default image
        }

        // Logout function
        binding.logoutCard.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ActivityLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Upload profile image
        binding.editProfileImage.setOnClickListener {
            openImagePicker()
        }
    }

    private fun fetchUserData(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("name") ?: "No Name"
                    val profileImageUrl = document.getString("profileImage")

                    binding.textViewUserName.text = userName

                    // Load profile image (default if null)
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(R.drawable.default_profile)
                    }
                }
            }
            .addOnFailureListener {
                binding.textViewUserName.text = "Error fetching name"
                binding.profileImage.setImageResource(R.drawable.default_profile)
            }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data!!.data
                selectedImageUri?.let {
                    binding.profileImage.setImageURI(it) // Preview selected image
                    uploadImageToFirebase(it)
                }
            }
        }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = firebaseAuth.currentUser ?: return
        val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateFirestoreWithImage(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFirestoreWithImage(imageUrl: String) {
        val user = firebaseAuth.currentUser ?: return
        val userRef = firestore.collection("users").document(user.uid)

        userRef.update("profileImage", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile image updated!", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(imageUrl).into(binding.profileImage) // Update UI
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
