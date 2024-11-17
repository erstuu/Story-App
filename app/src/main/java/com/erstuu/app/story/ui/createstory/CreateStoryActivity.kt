package com.erstuu.app.story.ui.createstory

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.erstuu.app.story.R
import com.erstuu.app.story.data.ResultState
import com.erstuu.app.story.databinding.ActivityCreateStoryBinding
import com.erstuu.app.story.ui.ViewModelFactory
import com.erstuu.app.story.ui.main.MainActivity
import com.erstuu.app.story.utils.getImageUri
import com.erstuu.app.story.utils.reduceFileImage
import com.erstuu.app.story.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding

    private val viewModel by viewModels<CreateStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private var currentLatitude: Float? = null
    private var currentLongitude: Float? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                checkLocationPermissionAndGetLocation { location ->
                    location?.let {
                        currentLatitude = it.latitude.toFloat()
                        currentLongitude = it.longitude.toFloat()
                    } ?: Toast.makeText(this, R.string.location_alert, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_LONG).show()
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationPermissionAndGetLocation(callback: (Location?) -> Unit) {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val location: Location? = task.result
                    callback(location)
                } else {
                    callback(null)
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, CreateStoryActivity::class.java))
                finish()
            } else {
                playAnimation()
                binding.btnGallery.setOnClickListener {
                    startGallery()
                }
                binding.btnCamera.setOnClickListener {
                    startCamera()
                }
                uploadStory()
            }
        }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermissionAndGetLocation { location ->
                    location?.let {
                        currentLatitude = it.latitude.toFloat()
                        currentLongitude = it.longitude.toFloat()
                    } ?: Toast.makeText(this, R.string.location_alert, Toast.LENGTH_LONG).show()
                }
            } else {
                currentLatitude = null
                currentLongitude = null
            }
        }

        val toolbar: Toolbar = findViewById<View>(R.id.topAppBar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun uploadStory() {
        binding.buttonAdd.setOnClickListener {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val description = binding.edAddDescription.text.toString()

                viewModel.uploadImage(imageFile, description, currentLatitude, currentLongitude).observe(this) { response ->
                    if (response != null) {
                        when (response) {
                            is ResultState.Loading -> {
                                binding.progressIndicator.isVisible = true
                            }

                            is ResultState.Success -> {
                                binding.progressIndicator.isVisible = false
                                showToast(response.data.message.toString())
                                val toMain = Intent(this, MainActivity::class.java)
                                toMain.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(toMain)
                            }

                            is ResultState.Error -> {
                                binding.progressIndicator.isVisible = false
                                showToast(response.error)
                            }
                        }
                    }
                }
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun startCamera() {
        val uri = getImageUri(this)
        currentImageUri = uri
        launcherIntentCamera.launch(uri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
            showToast(getString(R.string.image_cannot_be_uploaded))
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private fun showImage() {
        try {
            currentImageUri?.let {
                binding.uploadImage.setImageURI(it)
            } ?: showToast(getString(R.string.image_cannot_be_uploaded))
        } catch (e: Exception) {
            showToast(getString(R.string.image_cannot_be_uploaded))
        }
    }

    private fun playAnimation() {
        val photo = ObjectAnimator.ofFloat(binding.uploadImage, View.ALPHA, 1f).setDuration(100)
        val description =
            ObjectAnimator.ofFloat(binding.edAddDescription, View.ALPHA, 1f).setDuration(100)
        val descEditTextLayout =
            ObjectAnimator.ofFloat(binding.textDescInput, View.ALPHA, 1f).setDuration(100)
        val camera = ObjectAnimator.ofFloat(binding.btnCamera, View.ALPHA, 1f).setDuration(100)
        val gallery = ObjectAnimator.ofFloat(binding.btnGallery, View.ALPHA, 1f).setDuration(100)
        val upload = ObjectAnimator.ofFloat(binding.buttonAdd, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playTogether(
                camera,
                gallery
            )
            playSequentially(
                photo,
                description,
                descEditTextLayout,
                upload
            )
            startDelay = 100
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}