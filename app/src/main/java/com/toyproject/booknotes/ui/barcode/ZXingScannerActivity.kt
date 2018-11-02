package com.toyproject.booknotes.ui.barcode

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import com.toyproject.booknotes.R
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_zxing_scanner.*
import android.content.Intent
import android.view.View
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoClearedDisposable
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.toyproject.booknotes.ui.books.BookcaseActivity
import org.jetbrains.anko.*
import java.util.*


class ZXingScannerActivity:DaggerAppCompatActivity(){
    internal val disposable = AutoClearedDisposable(this)
    internal val viewDisposables = AutoClearedDisposable(this, false)
    lateinit var viewModel: BarcodeScanViewModel
    @Inject lateinit var viewModelFactory: BarcodeScanVIewModelFactory
    lateinit var intentIntegrator:IntentIntegrator
    var isSearching:Boolean = false

    companion object {
        val TAG = "ZXingScannerActivity"
        val RC_HANDLE_CAMERA_PERM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing_scanner)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[BarcodeScanViewModel::class.java]
        lifecycle += disposable
        lifecycle += viewDisposables

        viewDisposables += viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(it.isEmpty) {
                    } else {
                       if(!isSearching) {
                           var bookInfo: BookInfo = it.value[0] as BookInfo
                           showAddDialog(it.value[0] as BookInfo)
                           isSearching = true
                       }
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

        val rc:Int = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if(rc == PackageManager.PERMISSION_GRANTED) initScan()
        else requestCameraPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                finish()
            } else {
                disposable += viewModel.searchBook(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initScan(){
        intentIntegrator = IntentIntegrator(this)
        with(intentIntegrator){
            setPrompt(getString(R.string.add_book))
            setBeepEnabled(false)
            setOrientationLocked(true)
            setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
            initiateScan()
        }
    }

    private fun requestCameraPermission(){
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }
    }

    private fun showAddDialog(bookInfo:BookInfo){
        alert {
            val addBook = getString(R.string.add_msg)
            title = String.format(addBook, bookInfo.title)
            yesButton {
                var date = Calendar.getInstance().time
                with(bookInfo){
                    readStartDate = date
                    readEndDate = date
                }
                disposable += viewModel.addToBookBasic(bookInfo)
                startActivity(intentFor<BookcaseActivity>().clearTop())
            }
            noButton {
                isSearching = false
                initScan()
            }
        }.show()
    }

    private fun showError(message:String?){
    }

    private fun hideError(){
    }

    private fun showProgress(){
        pbActivityZxingScanner.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        pbActivityZxingScanner.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
