package com.toyproject.booknotes.ui.detail

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toyproject.booknotes.R
import kotlinx.android.synthetic.main.fragment_review.*
import java.lang.Exception
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.view.inputmethod.InputMethodManager

class ReviewInputFragment:Fragment() {

    lateinit var viewModel:DetailBookInfoViewModel
    private var viewLifecycleOwner:ViewLifecycleOwner? = null
    private var callBack:OnFragmentListener? = null

    companion object {
        fun newInstance():ReviewInputFragment{
            return ReviewInputFragment()
        }
    }

    interface OnFragmentListener {
        fun onFinish()
    }

    internal class ViewLifecycleOwner : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        override fun getLifecycle(): LifecycleRegistry {
            return lifecycleRegistry
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner = ViewLifecycleOwner()
        viewLifecycleOwner?.lifecycle!!.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner?.lifecycle!!.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner?.lifecycle!!.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        viewLifecycleOwner?.lifecycle!!.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        viewLifecycleOwner?.lifecycle!!.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner?.let{
            it.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            viewLifecycleOwner = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run{
            ViewModelProviders.of(this).get(DetailBookInfoViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        openSoftKeyboard(etReview)
        etReview.setText(viewModel.bookInfo.review)
        btReviewSave.setOnClickListener {
            viewModel.bookInfo.review = etReview.text.toString()
            hideSoftKeyboard(etReview)
            callBack?.onFinish()
        }

        btReviewCancel.setOnClickListener{
            hideSoftKeyboard(etReview)
            callBack?.onFinish()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            callBack = activity as OnFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnFragmentListener")
        }
    }

    private fun openSoftKeyboard(view: View) {
        view.requestFocus()
        // open the soft keyboard
        activity?.run{
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideSoftKeyboard(view: View){
        activity?.run{
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}