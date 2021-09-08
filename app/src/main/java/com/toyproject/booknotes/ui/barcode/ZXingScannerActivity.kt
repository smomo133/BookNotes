package com.toyproject.booknotes.ui.barcode

import android.Manifest
import com.toyproject.booknotes.R
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import android.content.Intent
import android.view.View
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoClearedDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.toyproject.booknotes.databinding.ActivityZxingScannerBinding
import com.toyproject.booknotes.ui.books.BookcaseActivity
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.*
import java.util.*

@AndroidEntryPoint
class ZXingScannerActivity:AppCompatActivity(){
    internal val disposable = AutoClearedDisposable(this)
    internal val viewDisposables = AutoClearedDisposable(this, false)
    private val viewModel:BarcodeScanViewModel by viewModels()
    lateinit var intentIntegrator:IntentIntegrator
    var isSearching:Boolean = false
    private lateinit var binding:ActivityZxingScannerBinding

    companion object {
        val TAG = "ZXingScannerActivity"
        val RC_HANDLE_CAMERA_PERM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityZxingScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.pbActivityZxingScanner.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.pbActivityZxingScanner.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
