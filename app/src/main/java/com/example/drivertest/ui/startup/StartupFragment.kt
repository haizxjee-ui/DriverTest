package com.example.drivertest.ui.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.drivertest.R
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.databinding.FragmentStartupErrorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class StartupFragment : Fragment() {

    private var _binding: FragmentStartupErrorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StartupViewModel by activityViewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartupErrorBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRetryButton()
        observeStartupState()
    }

    private fun navigateAfterStartup() {

        viewLifecycleOwner.lifecycleScope.launch {

            val selectedLicenseID =
                dataStoreManager.selectedLicenseTypeID.first()

            if (selectedLicenseID == null) {

                findNavController().navigate(
                    R.id.action_startupFragment_to_licenseSelectionFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.startupFragment) {
                            inclusive = true
                        }
                    }
                )

            } else {

                findNavController().navigate(
                    R.id.action_startupFragment_to_homeFragment,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.fade_in
                            exit = R.anim.fade_out
                            popEnter = R.anim.fade_in
                            popExit = R.anim.fade_out
                        }

                        popUpTo(R.id.startupFragment) {
                            inclusive = true
                        }
                    }
                )
            }
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun observeStartupState() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.state.collect { state ->

                    when (state) {

                        StartupState.Loading -> {
                            binding.errorCard.visibility = View.VISIBLE

                            binding.ivError.visibility = View.GONE
                            binding.tvErrorTitle.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.GONE

                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvLoadingMessage.visibility = View.VISIBLE

                            binding.btnRetry.visibility = View.GONE
                        }

                        is StartupState.Error -> {
                            binding.errorCard.visibility = View.VISIBLE

                            binding.ivError.visibility = View.VISIBLE
                            binding.tvErrorTitle.visibility = View.VISIBLE
                            binding.tvErrorMessage.visibility = View.VISIBLE

                            binding.progressBar.visibility = View.GONE
                            binding.tvLoadingMessage.visibility = View.GONE

                            binding.btnRetry.visibility = View.VISIBLE
                            binding.btnRetry.isEnabled = true

                            binding.tvErrorMessage.text = state.message
                        }

                        StartupState.Success -> {
                            navigateAfterStartup()

                            binding.errorCard.visibility = View.GONE
                        }

                        StartupState.NeedLicenseSelection -> {
                            findNavController().navigate(
                                R.id.action_startupFragment_to_licenseSelectionFragment,
                                null,
                                navOptions {
                                    popUpTo(R.id.startupFragment) {
                                        inclusive = true
                                    }
                                }
                            )
                            binding.errorCard.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}