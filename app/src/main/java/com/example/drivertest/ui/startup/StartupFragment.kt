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
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentStartupErrorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class StartupFragment : Fragment() {

    private var _binding: FragmentStartupErrorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StartupViewModel by activityViewModels()

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
                            binding.ivError.visibility = View.GONE
                            binding.tvErrorTitle.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnRetry.visibility = View.GONE

                            binding.tvErrorMessage.text = getString(R.string.error_message_binding)
                        }

                        is StartupState.Error -> {
                            binding.ivError.visibility = View.VISIBLE
                            binding.tvErrorTitle.visibility = View.VISIBLE
                            binding.tvErrorMessage.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.btnRetry.visibility = View.VISIBLE

                            binding.btnRetry.isEnabled = true
                            binding.tvErrorMessage.text = state.message
                        }

                        StartupState.Success -> {
                            findNavController().navigate(
                                R.id.action_startupFragment_to_homeFragment
                            )
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