package com.toyproject.booknotes.ui.barcode

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.ui.camera.CameraSourcePreview
import com.toyproject.booknotes.ui.camera.GraphicOverlay
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import org.jetbrains.anko.longToast
import java.io.File
import java.io.IOException
import kotlinx.android.synthetic.main.activity_barcode_scan.*
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoClearedDisposable
import io.reactivex.android.schedulers.AndroidSchedulers


class BarcodeScanActivity:DaggerAppCompatActivity(), BarcodeGraphicTracker.BarcodeUpdateListener{

    private var barcodeDetector: BarcodeDetector? = null
    private var mCameraSource: CameraSource? = null

    internal val disposable = AutoClearedDisposable(this)
    internal val viewDisposables = AutoClearedDisposable(this, false)
    lateinit var viewModel: BarcodeScanViewModel
    @Inject lateinit var viewModelFactory: BarcodeScanVIewModelFactory

    lateinit var mGraphicOverlay:GraphicOverlay<BarcodeGraphic>
    lateinit var mPreview:CameraSourcePreview

    companion object {
        val TAG =  "BarcodeScanActivity"
        private val RC_HANDLE_GMS:Int = 9001
        private val RC_HANDLE_CAMERA_PERM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scan)
        setSupportActionBar(tbBarcodeTop)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[BarcodeScanViewModel::class.java]

        lifecycle += disposable
        lifecycle += viewDisposables

        viewDisposables += viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(it.isEmpty) {
                    } else {
                        var book:BookInfo = it.value[0] as BookInfo
                        longToast("list size :   " + book.title)
                    }
                }

        viewDisposables += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message ->
                    if(message.isEmpty) hideError()
                    else                showError(message.value)
                }

        viewDisposables += viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {isLoading ->
                    if(isLoading)   showProgress()
                    else            hideProgress()
                }


        mPreview = findViewById(R.id.preview) as CameraSourcePreview
        mGraphicOverlay = findViewById(R.id.graphicOverlay) as GraphicOverlay<BarcodeGraphic>

        val rc:Int = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if(rc == PackageManager.PERMISSION_GRANTED) createCameraSource()
        else requestCameraPermission()
    }

    private fun createCameraSource(){
        barcodeDetector = createBarcodeDetector()

        barcodeDetector?.let {
            if (!it.isOperational) {
                val cacheDir: File = getCacheDir()
                if (cacheDir.usableSpace * 100 / cacheDir.totalSpace <= 100) {
                    longToast(R.string.low_storage_error)
                }
            }
        }

        mCameraSource = CameraSource.Builder(this@BarcodeScanActivity, barcodeDetector)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1600, 1024)
                        .setRequestedFps(15.0f)
                        .setAutoFocusEnabled(true)
                        .build()

    }

    private fun requestCameraPermission(){
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            createCameraSource()
            return
        }

        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show()
    }

    @Throws(SecurityException::class)
    private fun startCameraSource(){
        val code:Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if(code != ConnectionResult.SUCCESS) {
            val diglog:Dialog
                    = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            diglog.show()
        }

        mCameraSource?.let{
            try {
                mPreview.start(it, mGraphicOverlay)
            } catch (e: IOException) {
                it.release()
            }
        }
    }

    private fun createBarcodeDetector():BarcodeDetector{
        val barcodeDetector = BarcodeDetector.Builder(this@BarcodeScanActivity)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        val barcodeFactory = BarcodeTrackerFactory(mGraphicOverlay, this)
        barcodeDetector.setProcessor(MultiProcessor.Builder(barcodeFactory).build())
        return barcodeDetector
    }

    override fun onBarcodeDetected(barcode: Barcode?) {
        barcode?.let{
            disposable += viewModel.searchBook(it.rawValue)
        }
    }

    private fun showError(message:String?){
    }

    private fun hideError(){
    }

    private fun showProgress(){
        pbActivityBarcode.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        pbActivityBarcode.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        if(mPreview != null)    mPreview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mPreview != null)    mPreview.release()
    }
 }