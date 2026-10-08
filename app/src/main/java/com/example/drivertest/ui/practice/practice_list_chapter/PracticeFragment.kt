package com.example.drivertest.ui.practice.practice_list_chapter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentPracticeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PracticeFragment : Fragment(R.layout.fragment_practice) {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PracticeViewModel by viewModels()

    private lateinit var chapterAdapter: ChapterAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPracticeBinding.bind(view)

        setupRecyclerView()
        setupBackButton()
        observeChapters()
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshChapters()
    }

    private fun setupRecyclerView() {

        chapterAdapter = ChapterAdapter(

            onChapterClick = { chapter ->

                val bundle = Bundle().apply {
                    putInt(
                        "chapterNumber",
                        chapter.chapterNumber
                    )

                    putInt(
                        "questionTypeID",
                        chapter.questionTypeID
                    )
                }

                findNavController().navigate(
                    R.id.action_practiceFragment_to_practiceQuestionFragment,
                    bundle
                )
            },

            onResetClick = { chapter ->

                viewModel.resetChapter(
                    chapter.chapterNumber
                )
            }
        )

        binding.rvChapters.apply {

            layoutManager =
                LinearLayoutManager(requireContext())

            adapter =
                chapterAdapter

            setHasFixedSize(true)
        }
    }

    private fun setupBackButton() {

        binding.btnBack.setOnClickListener {

            findNavController().navigateUp()
        }
    }

    private fun observeChapters() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.chapters.collect { chapters ->

                    chapterAdapter.submitList(chapters)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}