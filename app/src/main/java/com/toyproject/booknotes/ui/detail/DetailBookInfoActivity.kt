package com.toyproject.booknotes.ui.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.toyproject.booknotes.R
import com.toyproject.booknotes.api.model.BookInfo
import com.toyproject.booknotes.databinding.ActivityDetailBookinfoBinding
import com.toyproject.booknotes.extension.plusAssign
import com.toyproject.booknotes.rx.AutoClearedDisposable
import com.toyproject.booknotes.util.TextUtil
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.*
import java.util.*

@AndroidEntryPoint
class DetailBookInfoActivity: AppCompatActivity(), ReviewInputFragment.OnFragmentListener {

    internal val disposable = AutoClearedDisposable(this)
    internal val viewDisposable =
            AutoClearedDisposable(this, false)
    private val viewModel:DetailBookInfoViewModel by viewModels()
    var reviewFragment:ReviewInputFragment? = null
    private lateinit var binding:ActivityDetailBookinfoBinding

    companion object {
        const val TAG = "DetailBookInfoActivity"
        const val BOOK_INFO = "book_info"
        const val START_DAY = "start_day"
        const val END_DAY = "end_day"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBookinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbDetailPage)

        lifecycle += disposable
        lifecycle += viewDisposable

        viewModel.bookInfo = intent.getSerializableExtra(BOOK_INFO) as BookInfo
        detailPageInit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_book_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_activity_detail_save -> {
                alert(getString(R.string.save_msg)) {
                    yesButton {
                        viewModel.updateBookInfo(viewModel.bookInfo)
                        finish()
                    }
                    noButton { }
                }.show()
                return true
            }

            R.id.menu_activity_detail_delete -> {
                alert(getString(R.string.delete_msg)) {
                    yesButton {
                        viewModel.removeBookInfo(viewModel.bookInfo)
                        finish()
                    }
                    noButton { }
                }.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFinish() {
        reviewFragment?.let {
            supportFragmentManager.beginTransaction().remove(reviewFragment!!).commit()
        }
        supportActionBar!!.show()
        viewModel.bookInfo.review?.let{
            binding.tvWriteReview.text = it
        }
    }

    private fun detailPageInit(){
        viewModel.bookInfo?.let{
            Glide
                    .with(applicationContext)
                    .load(it.thumbnail)
                    .into(binding.ivDetailBookThumb)
            binding.tvDetailBookTitle.text = it.title
            binding.tvDetailBookAuthor.text = TextUtil.commaEllipsize(it.authors)
            binding.tvDetailBookPublisher.text = it.publisher
            it.translators?.let {
                binding.tvDetailBookTranslator.text = TextUtil.commaEllipsize(it)
            }
            it.datetime?.let{
                binding.tvDetailBookDatetime.text = TextUtil.convertISOFormatDateStr(it)
            }
            it.price?.let{
                binding.tvDetailBookPrice.text = it.toString() + getString(R.string.won)
            }
            with(binding.tvDetailBookPurchase){
                it.purchase?.let{setText(it)}
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(text: Editable) {
                        text?.let {
                            viewModel.bookInfo.purchase = it.toString()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                })
            }

            //
            with(binding.tvDetailBookReadStartDate){
                setReadStartDay(it.readStartDate)
                setOnClickListener { showDateDialog(viewModel.bookInfo.readStartDate, START_DAY) }
            }
            //
            with(binding.tvDetailBookReadEndDate){
                setReadEndDay(it.readEndDate)
                setOnClickListener { showDateDialog(viewModel.bookInfo.readEndDate, END_DAY) }
            }
            //
            with(binding.rgBookType){
                (getChildAt(it.bookType) as RadioButton).isChecked = true
                setOnCheckedChangeListener { radioGroup, i ->
                    when(i){
                        R.id.rbPaperBook -> it.bookType = 0
                        R.id.rbEBook -> it.bookType = 1
                        R.id.rbEtcBook -> it.bookType = 2
                    }
                }
            }
            //
            with(binding.ratingBookGrade){
                this.rating = it.grade
                setOnRatingBarChangeListener { ratingBar, fl, b ->
                    it.grade = fl
                }
            }
            with(binding.tvWriteReview){
                this.text = it.review
                setOnClickListener { showReviewFragment() }
            }
        }
    }

    private fun showReviewFragment(){
        reviewFragment = ReviewInputFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.flContents, reviewFragment!!)
                .commit()
        supportActionBar!!.hide()
    }

    private fun showDateDialog(date: Date, type: String){
        alert {
            lateinit var datePicker: DatePicker
            var calendar = Calendar.getInstance()
            customView {
                verticalLayout {
                    datePicker = datePicker {
                    calendar.time = date
                        this.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH), null)
                    }
                }
            }
            yesButton {
                var parsedDate = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"
                calendar?.let {
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    var date = calendar.time
                    if(type == START_DAY)       setReadStartDay(calendar.time, true)
                    else if(type == END_DAY)    setReadEndDay(calendar.time, true)
                }
            }
            noButton {
            }
        }.show()
    }

    private fun setReadStartDay(date: Date, isSet: Boolean = false){
        binding.tvDetailBookReadStartDate.text = getString(R.string.readStartDate) + " : " + TextUtil.convertDateToStr(date)
        if(isSet)   viewModel.bookInfo.readStartDate = date
    }

    private fun setReadEndDay(date: Date, isSet: Boolean = false){
        binding.tvDetailBookReadEndDate.text = getString(R.string.readEndDate) + " : " + TextUtil.convertDateToStr(date)
        if(isSet)   viewModel.bookInfo.readEndDate = date
    }
}