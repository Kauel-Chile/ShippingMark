package com.kauel.shippingmark.ui.scandit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kauel.shippingmark.R
import com.kauel.shippingmark.databinding.FragmentScanditBinding
import com.kauel.shippingmark.utils.KEY_SCANDIT
import com.kauel.shippingmark.utils.lifeCycleNavigate
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanditFragment : Fragment(R.layout.fragment_scandit), BarcodeCaptureListener {

    private var binding: FragmentScanditBinding? = null

    private var dataCaptureContext: DataCaptureContext =
        DataCaptureContext.forLicenseKey(KEY_SCANDIT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentScanditBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        cameraOff()
        super.onPause()
    }

    override fun onResume() {
        cameraOn()
        super.onResume()
    }

    private fun init() {
        scandit()
    }

    private fun scandit() {
        val settings = BarcodeCaptureSettings()
        settings.apply {
            enableSymbology(Symbology.CODE128, true)
            enableSymbology(Symbology.CODE39, true)
            //enableSymbology(Symbology.QR, true)
            enableSymbology(Symbology.EAN8, true)
            enableSymbology(Symbology.UPCE, true)
            enableSymbology(Symbology.EAN13_UPCA, true)
        }

        val barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings);

        barcodeCapture.addListener(this)

        cameraOn()

        val dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext)

        binding?.lScandit?.addView(dataCaptureView)

        //val overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView)
    }

    override fun onBarcodeScanned(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        data: FrameData,
    ) {
        val recognizedBarcodes = session.newlyRecognizedBarcodes[0]
        val code = recognizedBarcodes.data
        saveData(code!!)
        findNavController().lifeCycleNavigate(lifecycleScope, R.id.fragmentMain)
    }

    private fun saveData(code: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("CODE_SCANDIT", code)
            apply()
        }
    }

    private fun cameraOn() {
        val cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        val camera = Camera.getDefaultCamera()
        camera?.applySettings(cameraSettings)
        dataCaptureContext.setFrameSource(camera)

        camera?.switchToDesiredState(FrameSourceState.ON)
    }

    private fun cameraOff() {
        val cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        val camera = Camera.getDefaultCamera()
        camera?.applySettings(cameraSettings)
        dataCaptureContext.setFrameSource(camera)

        camera?.switchToDesiredState(FrameSourceState.OFF)
    }

    override fun onDestroy() {
        cameraOff()
        binding = null
        super.onDestroy()
    }

}