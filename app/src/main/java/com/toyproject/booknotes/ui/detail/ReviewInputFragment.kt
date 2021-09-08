package com.toyproject.booknotes.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.Exception
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import com.toyproject.booknotes.databinding.FragmentReviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewInputFragment: Fragment() {

    lateinit var viewModel:DetailBookInfoViewModel
    private var viewLifecycleOwner:ViewLifecycleOwner? = null
    private var callBack:OnFragmentListener? = null

    private var _binding:FragmentReviewBinding? = null
    private val binding get() = _binding!!

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

        openSoftKeyboard(binding.etReview)
        binding.etReview.setText(viewModel.bookInfo.review)
        binding.btReviewSave.setOnClickListener {
            viewModel.bookInfo.review = binding.etReview.text.toString()
            hideSoftKeyboard(binding.etReview)
            callBack?.onFinish()
        }

        binding.btReviewCancel.setOnClickListener{
            hideSoftKeyboard(binding.etReview)
            callBack?.onFinish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwner = ViewLifecycleOwner()
        viewLifecycleOwner?.lifecycle!!.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        viewModel = activity?.run{
            ViewModelProvider(this).get(DetailBookInfoViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
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
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
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