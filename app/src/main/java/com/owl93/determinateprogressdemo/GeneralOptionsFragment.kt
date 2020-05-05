package com.owl93.determinateprogressdemo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.owl93.determinateprogressdemo.databinding.FragmentGeneralOptionsBinding
import com.owl93.dpb.Direction
import kotlinx.android.synthetic.main.fragment_general_options.*
import java.lang.NumberFormatException

class GeneralOptionsFragment: Fragment() {
    val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGeneralOptionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //direction
        ccw_switch.setOnCheckedChangeListener { _, checked ->
            viewModel.direction.value = if(checked) Direction.CCW else Direction.CW
        }

        //track
        track_switch.setOnCheckedChangeListener{ _, checked -> viewModel.drawTrack.value = checked }

        //stroke and text size
        stroke_size.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { viewModel.strokeSize.value = progress.toFloat() }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        track_size.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { viewModel.trackSize.value = progress.toFloat() }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        //starting angle
        starting_angle.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { viewModel.startingAngle.value = progress}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //progress input
        progress_input.setEndIconOnClickListener {
            viewModel.currentProgress.value = 65f
            progress_input.editText?.setText(65f.toString())
        }


        viewModel.currentProgress.observe(viewLifecycleOwner, Observer {

        })
        progress_input.editText?.apply {
            addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    var newValue = 0f
                    try{
                        newValue = s.toString().toFloat()
                    }catch (nfe: NumberFormatException){
                        Log.d(TAG,"whoops, bad number typed in progress value")
                    }finally {
                        viewModel.currentProgress.postValue(newValue)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        //max value input
        progress_maxvalue.setEndIconOnClickListener {
            viewModel.maxValue.value = 100f
            progress_maxvalue.editText?.setText(100f.toString())
        }

        progress_maxvalue.editText?.let {
            it.setText(viewModel.maxValue.value!!.toString())
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    var newValue = 100f
                    try {
                        newValue = s.toString().toFloat()
                    } catch (nfe: NumberFormatException) {
                        Log.d(TAG, "whoops, bad number typed in max value")
                    } finally {
                        viewModel.maxValue.postValue(newValue)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        animation_duration.editText?.apply {
            setText(viewModel.animationDuration.value!!.toString())
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    var newValue = 200L
                    try {
                        newValue = p0.toString().toLong()
                    }catch (nfe: NumberFormatException) {
                        Log.d(TAG, "whoops, bad number in animation duration value (${p0.toString()}")
                    }finally {
                        viewModel.animationDuration.postValue(newValue)
                    }
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }

        //animation interpolator
        val rows: List<MutableMap<String, String>> = viewModel.interpolatorNames.map { mutableMapOf("name" to it) }
        val adapter = SimpleAdapter(context, rows, android.R.layout.simple_dropdown_item_1line, arrayOf("name"), intArrayOf(android.R.id.text1))
        interpolator_spinner.adapter = adapter
        interpolator_spinner.setSelection(0)
        interpolator_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.animationInterpolator.value = viewModel.interpolators[p2]
            }
        }
    }

    companion object {
        const val TAG = "GeneralOptions"
    }
}