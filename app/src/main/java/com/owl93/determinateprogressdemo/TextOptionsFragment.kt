package com.owl93.determinateprogressdemo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.owl93.determinateprogressdemo.databinding.FragmentTextOptionsBinding
import com.owl93.dpb.TextFormat
import kotlinx.android.synthetic.main.fragment_text_options.*


class TextOptionsFragment : Fragment() {
    val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTextOptionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        show_text.setOnCheckedChangeListener { _, checked -> viewModel.showText.postValue(checked) }

        text_size.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { viewModel.textSize.value = progress.toFloat() }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //text format
        //val fmtRows: List<MutableMap<String, String>> = viewModel.formatOptions.map { mutableMapOf("name" to it) }
        //val fmtAdapter = SimpleAdapter(requireContext(), fmtRows, android.R.layout.simple_dropdown_item_1line, arrayOf("name"), intArrayOf(android.R.id.text1))
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, viewModel.formatOptions)
        format_spinner.adapter = adapter
        format_spinner.setSelection(when(viewModel.textFormat.value!!) {
            TextFormat.PERCENT -> 0
            TextFormat.DECIMAL_PERCENT -> 1
            TextFormat.INT -> 2
            TextFormat.FLOAT -> 3
        })
        format_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.textFormat.value = when(p2) {
                    1 -> TextFormat.DECIMAL_PERCENT
                    2 -> TextFormat.INT
                    3 -> TextFormat.FLOAT
                    else -> TextFormat.PERCENT
                }
            }
        }

        //custom text
        custom_text.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                viewModel.customText.value = if(p0.toString().isBlank()) null
                else p0.toString()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

}