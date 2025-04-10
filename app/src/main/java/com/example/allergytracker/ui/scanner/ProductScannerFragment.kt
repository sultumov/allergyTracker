package com.example.allergytracker.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentProductScannerBinding
import com.example.allergytracker.viewmodel.ProductScannerViewModel
import com.google.android.gms.vision.CameraSource
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.camera.CameraSourceConfig
import com.google.mlkit.vision.camera.DetectionTask
import com.google.mlkit.vision.camera.CameraSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductScannerFragment : Fragment() {
    private var _binding: FragmentProductScannerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductScannerViewModel by viewModels()
    private var cameraSource: CameraSource? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Snackbar.make(
                binding.root,
                "Camera permission is required for barcode scanning",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
        checkCameraPermission()
    }

    private fun setupClickListeners() {
        binding.btnScan.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.scannedProduct.collectLatest { product ->
                product?.let {
                    // Navigate to product details
                    findNavController().navigate(
                        ProductScannerFragmentDirections.actionProductScannerToProductDetails(product)
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val barcodeScanner = BarcodeScanning.getClient()
        
        val config = CameraSourceConfig.Builder(requireContext())
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 720)
            .setRequestedFps(15.0f)
            .build()

        cameraSource = CameraSource(config, object : DetectionTask<Barcode> {
            override fun onSuccess(results: List<Barcode>) {
                results.firstOrNull()?.rawValue?.let { barcode ->
                    viewModel.scanProduct(barcode)
                }
            }

            override fun onFailure(e: Exception) {
                viewModel.error.value = e.message
            }
        })

        cameraSource?.start(binding.previewView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraSource?.stop()
        _binding = null
    }
} 