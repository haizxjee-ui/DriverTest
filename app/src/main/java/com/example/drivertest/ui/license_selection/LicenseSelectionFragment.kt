package com.example.drivertest.ui.license_selection

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentLicenseSelectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions

@AndroidEntryPoint
class LicenseSelectionFragment :
    Fragment(R.layout.fragment_license_selection) {

    private var _binding: FragmentLicenseSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LicenseSelectionViewModel by viewModels()

    private lateinit var licenseSelectionAdapter: LicenseSelectionAdapter

    private var selectedLicenseTypeID: Int? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLicenseSelectionBinding.bind(view)

        setupRecyclerView()
        observeLicenseTypes()
        setupContinueButton()

        // Tự động load và chọn sẵn hạng bằng đã lưu gần nhất (nếu có)
        viewLifecycleOwner.lifecycleScope.launch {
            val savedID = viewModel.getSavedLicenseId()
            if (savedID != null && selectedLicenseTypeID == null) {
                selectedLicenseTypeID = savedID
                licenseSelectionAdapter.setSelectedLicenseId(savedID)
            }
        }
    }

    private fun setupContinueButton() {

        binding.btnContinue.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch {
                val savedID = viewModel.getSavedLicenseId()
                val selectedID = selectedLicenseTypeID ?: savedID

                if (selectedID == null) {
                    Toast.makeText(
                        requireContext(),
                        "Vui lòng chọn hạng",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                viewModel.saveSelectedLicense(selectedID)

                // Sau khi lưu xong mới chuyển sang Home
                findNavController().navigate(
                    R.id.action_licenseSelectionFragment_to_homeFragment,
                    null,
                    navOptions {
                        launchSingleTop = true

                        popUpTo(R.id.licenseSelectionFragment) {
                            inclusive = true
                        }

                        anim {
                            enter = R.anim.fade_in
                            exit = R.anim.fade_out
                            popEnter = R.anim.fade_in
                            popExit = R.anim.fade_out
                        }
                    }
                )
            }
        }
    }

    private fun setupRecyclerView() {


        licenseSelectionAdapter = LicenseSelectionAdapter(
            licenseTypes = emptyList(),
            onLicenseClick = { licenseType ->

                selectedLicenseTypeID = licenseType.licenseTypeID

            }
        )

        binding.rvLicenseTypes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = licenseSelectionAdapter
        }
    }


    private fun observeLicenseTypes() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.licenseTypes.collect { licenseTypes ->

                    licenseSelectionAdapter.submitList(licenseTypes)

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
