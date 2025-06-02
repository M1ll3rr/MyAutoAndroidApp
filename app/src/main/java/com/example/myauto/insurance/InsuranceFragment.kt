package com.example.myauto.insurance

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.databinding.FragmentInsuranceBinding
import com.example.myauto.unified.UnifiedViewModelFactory
import com.google.android.material.transition.MaterialSharedAxis
import com.rajat.pdfviewer.PdfRendererView
import dev.androidbroadcast.vbpd.viewBinding


class InsuranceFragment : Fragment() {
    private val binding: FragmentInsuranceBinding by viewBinding(FragmentInsuranceBinding::bind)
    private val viewModel: InsuranceViewModel by viewModels {
        val repository = (requireActivity() as MainActivity).getRepository()
        UnifiedViewModelFactory(repository)
    }

    private val getFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistUriPermissions(it)
            saveFile(it)
            displayFile(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_insurance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSavedFile()

        binding.selectFileButton.setOnClickListener {
            getFile.launch(arrayOf("image/*", "application/pdf"))
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun persistUriPermissions(uri: Uri) {
        requireContext().contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    private fun saveFile(uri: Uri) {
        viewModel.saveFile(uri)
        binding.fileContainer.visibility = View.VISIBLE
    }

    private fun loadSavedFile() {
        viewModel.getFileUri { uriString ->
            uriString?.let {
                binding.progressBar.visibility = View.VISIBLE
                val uri = Uri.parse(it)
                if (isUriAccessible(uri)) {
                    displayFile(uri)
                    binding.fileContainer.postDelayed({
                        binding.fileContainer.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }, 200)
                } else {
                    viewModel.deleteFile()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun isUriAccessible(uri: Uri): Boolean {
        return try {
            requireContext().contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun displayFile(uri: Uri) {
        try {
            with(binding) {
                photoView.visibility = View.GONE
                pdfView.visibility = View.GONE
            }
            val mimeType = requireContext().contentResolver.getType(uri)
            when (mimeType) {
                "application/pdf" -> displayPdf(uri)
                else -> displayImage(uri)
            }
        } catch (e: Exception) {
            showErrorDialog("${getString(R.string.error_displaying)}: ${e.localizedMessage}")
        }
    }

    private fun displayImage(uri: Uri) {
        val bitmap =
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    requireContext().contentResolver,
                    uri
                )
            )

        with(binding) {
            photoView.setImageBitmap(bitmap)
            photoView.visibility = View.VISIBLE
        }
    }

    private fun displayPdf(uri: Uri) {
        with(binding) {
            pdfView.initWithUri(uri)
            pdfView.statusListener = object : PdfRendererView.StatusCallBack {
                override fun onPdfLoadSuccess(absolutePath: String) {
                    pdfView.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}

