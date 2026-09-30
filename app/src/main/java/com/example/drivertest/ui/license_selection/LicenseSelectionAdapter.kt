package com.example.drivertest.ui.license_selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import com.example.drivertest.databinding.ItemLicenseTypeBinding

class LicenseSelectionAdapter(
    private var licenseTypes: List<LicenseTypeEntity>,
    private val onLicenseClick: (LicenseTypeEntity) -> Unit
) : RecyclerView.Adapter<LicenseSelectionAdapter.LicenseViewHolder>() {

    private var selectedLicenseId: Int? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LicenseViewHolder {

        val binding = ItemLicenseTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LicenseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LicenseViewHolder,
        position: Int
    ) {
        val licenseType = licenseTypes[position]

        holder.bind(licenseType)
    }

    override fun getItemCount(): Int {
        return licenseTypes.size
    }

    fun submitList(newList: List<LicenseTypeEntity>) {
        licenseTypes = newList
        notifyDataSetChanged()
    }

    inner class LicenseViewHolder(
        private val binding: ItemLicenseTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(licenseType: LicenseTypeEntity) {

            binding.tvLicenseName.text =
                "Hạng " + licenseType.name

            binding.tvTotalQuestions.text =
                licenseType.totalQuestion.toString() + " Câu"

            binding.tvDescription.text =
                licenseType.description

            binding.tvTime.text =
                licenseType.examTime.toString() + " Phút"

            binding.tvPassingScore.text =
                licenseType.passingScore.toString() +
                        "/" +
                        licenseType.totalQuestion.toString()

            // RadioButton
            binding.radioButton.isChecked =
                selectedLicenseId == licenseType.licenseTypeID

            // Viền Card
            binding.cardContainer.isSelected =
                selectedLicenseId == licenseType.licenseTypeID

            // Nhấn vào Card
            binding.cardContainer.setOnClickListener {

                selectedLicenseId = licenseType.licenseTypeID

                notifyDataSetChanged()

                onLicenseClick(licenseType)
            }

            // Nhấn trực tiếp vào RadioButton
            binding.radioButton.setOnClickListener {

                selectedLicenseId = licenseType.licenseTypeID

                notifyDataSetChanged()

                onLicenseClick(licenseType)
            }
        }
    }
}