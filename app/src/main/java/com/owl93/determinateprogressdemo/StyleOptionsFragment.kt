package com.owl93.determinateprogressdemo

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.owl93.determinateprogressdemo.databinding.FragmentStyleOptionsBinding
import com.owl93.dpb.Gradient
import com.owl93.dpb.StrokeGradient
import com.owl93.dpb.TextGradient
import kotlinx.android.synthetic.main.fragment_style_options.*
import kotlinx.android.synthetic.main.fragment_style_options.view.*
import kotlinx.android.synthetic.main.gradient_config_layout.view.*
import kotlinx.android.synthetic.main.gradient_config_layout.view.linear_angle_spinner
import java.lang.IllegalArgumentException

class StyleOptionsFragment: Fragment() {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var strokeGradientVG: ViewGroup
    private lateinit var textGradientVG: ViewGroup
    private var errorColor: Int = -1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentStyleOptionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorColor = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
        strokeGradientVG = view.stroke_gradient_controller as ViewGroup
        textGradientVG = view.text_gradient_controller as ViewGroup
        setUpStrokeStyleControls()
        setUpTextStyleControls()
        setUpTrackControls()
    }


    private fun setUpStrokeStyleControls() {
        //listeners
        stroke_style_radiogroup.setOnCheckedChangeListener { _, which ->
            viewModel.strokeStyleMode.value = when(which) {
                R.id.stroke_style_gradient_radio -> MainActivityViewModel.StyleMode.GRADIENT
                R.id.stroke_style_color_radio -> MainActivityViewModel.StyleMode.SOLID
                else -> throw IllegalArgumentException("invalid id supplied to stroke radio group listener")
            }
        }
        stroke_gradient_size_checkbox.setOnCheckedChangeListener { _, checked ->
            viewModel.strokeGradientSize.value = if(checked) StrokeGradient.STROKE_ONLY
            else StrokeGradient.VIEW
        }
        stroke_solid_swatch.setOnClickListener { showColorPicker(viewModel.strokeSolidColor) }
        strokeGradientVG.apply {
            initSpinner(gradient_type_spinner, viewModel.strokeGradientStyle, viewModel.gradientStyleNames, viewModel.gradientStyles)
            gradient_type_spinner.setSelection(when(viewModel.strokeGradientStyle.value!!) {
                Gradient.STYLE_SWEEP -> 0
                Gradient.STYLE_LINEAR -> 1
                Gradient.STYLE_RADIAL -> 2
            })
            gradient_start_color.setOnClickListener { showColorPicker(viewModel.strokeGradientStartColor) }
            gradient_center_color.setOnClickListener { showColorPicker(viewModel.strokeGradientCenterColor, true) }
            gradient_end_color.setOnClickListener { showColorPicker(viewModel.strokeGradientEndColor) }
            initSpinner(linear_angle_spinner, viewModel.strokeLinearGradientAngle, viewModel.linearAngles, viewModel.linearAngles)
            linear_angle_spinner.setSelection(viewModel.linearAngles.indexOf(viewModel.strokeLinearGradientAngle.value!!))
        }
        //observers
        viewModel.strokeStyleMode.observe(viewLifecycleOwner, Observer {
            TransitionManager.beginDelayedTransition(style_options_container, AutoTransition())
            if(it == MainActivityViewModel.StyleMode.GRADIENT){
                strokeGradientVG.stroke_gradient_controller.visibility = View.VISIBLE
                stroke_gradient_size_checkbox.visibility = View.VISIBLE
                stroke_solid_swatch.visibility = View.GONE
            }else {
                strokeGradientVG.stroke_gradient_controller.visibility = View.GONE
                stroke_gradient_size_checkbox.visibility = View.GONE
                stroke_solid_swatch.visibility = View.VISIBLE
            }
        })
        viewModel.strokeGradientStyle.observe(viewLifecycleOwner, Observer {
            strokeGradientVG.linear_angle_spinner.isEnabled = it == Gradient.STYLE_LINEAR
        })
        viewModel.strokeGradientStartColor.observe(viewLifecycleOwner, Observer {
            strokeGradientVG.gradient_start_color.imageTintList = ColorStateList.valueOf(it)
        })
        viewModel.strokeGradientCenterColor.observe(viewLifecycleOwner, Observer {
            strokeGradientVG.gradient_center_color.apply {
                setImageResource(if(it == Color.TRANSPARENT) R.drawable.ic_no_color else R.drawable.color_swatch)
                imageTintList = ColorStateList.valueOf(if(it == Color.TRANSPARENT) errorColor else it)
            }
        })
        viewModel.strokeGradientEndColor.observe(viewLifecycleOwner, Observer {
            strokeGradientVG.gradient_end_color.imageTintList = ColorStateList.valueOf(it)
        })
        viewModel.strokeSolidColor.observe(viewLifecycleOwner, Observer {
            stroke_solid_swatch.imageTintList = ColorStateList.valueOf(it)
        })

    }



    private fun setUpTextStyleControls() {
        //listeners
        text_style_radiogroup.setOnCheckedChangeListener { _, which ->
            viewModel.textStyleMode.value = when(which) {
                R.id.text_style_color_radio -> MainActivityViewModel.StyleMode.SOLID
                R.id.text_style_gradient_radio -> MainActivityViewModel.StyleMode.GRADIENT
                else -> throw IllegalArgumentException("invalid id supplied to stroke radio group listener")
            }
        }
        text_gradient_size_checkbox.setOnCheckedChangeListener { _, checked ->
            viewModel.textGradientSize.value = if (checked) TextGradient.TEXT_ONLY
            else TextGradient.VIEW
        }

        text_solid_swatch.setOnClickListener { showColorPicker(viewModel.textSolidColor) }
        textGradientVG.apply {
            initSpinner(gradient_type_spinner, viewModel.textGradientStyle, viewModel.gradientStyleNames, viewModel.gradientStyles)
            gradient_type_spinner.setSelection(when(viewModel.textGradientStyle.value!!) {
                Gradient.STYLE_SWEEP -> 0
                Gradient.STYLE_LINEAR -> 1
                Gradient.STYLE_RADIAL -> 2
            })
            gradient_start_color.setOnClickListener { showColorPicker(viewModel.textGradientStartColor) }
            gradient_center_color.setOnClickListener { showColorPicker(viewModel.textGradientCenterColor, true) }
            gradient_end_color.setOnClickListener { showColorPicker(viewModel.textGradientEndColor) }
            initSpinner(linear_angle_spinner, viewModel.textLinearGradientAngle, viewModel.linearAngles, viewModel.linearAngles)
            linear_angle_spinner.setSelection(viewModel.linearAngles.indexOf(viewModel.textLinearGradientAngle.value!!))
        }
        //observers
        viewModel.textStyleMode.observe(viewLifecycleOwner, Observer {
            TransitionManager.beginDelayedTransition(style_options_container, AutoTransition())
            if(it == MainActivityViewModel.StyleMode.GRADIENT){
                textGradientVG.text_gradient_controller.visibility = View.VISIBLE
                text_gradient_size_checkbox.visibility = View.VISIBLE
                text_solid_swatch.visibility = View.GONE
            }else {
                textGradientVG.text_gradient_controller.visibility = View.GONE
                text_gradient_size_checkbox.visibility = View.GONE
                text_solid_swatch.visibility = View.VISIBLE
            }
        })

        viewModel.textGradientStyle.observe(viewLifecycleOwner, Observer {
            textGradientVG.linear_angle_spinner.isEnabled = it == Gradient.STYLE_LINEAR
        })
        viewModel.textGradientStartColor.observe(viewLifecycleOwner, Observer {
            textGradientVG.gradient_start_color.imageTintList = ColorStateList.valueOf(it)
        })
        viewModel.textGradientCenterColor.observe(viewLifecycleOwner, Observer {
            textGradientVG.gradient_center_color.apply {
                setImageResource(if(it == Color.TRANSPARENT) R.drawable.ic_no_color else R.drawable.color_swatch)
                imageTintList = ColorStateList.valueOf(if(it == Color.TRANSPARENT) errorColor else it)
            }
        })
        viewModel.textGradientEndColor.observe(viewLifecycleOwner, Observer {
            textGradientVG.gradient_end_color.imageTintList = ColorStateList.valueOf(it)
        })
        viewModel.textSolidColor.observe(viewLifecycleOwner, Observer {
            text_solid_swatch.imageTintList = ColorStateList.valueOf(it)
        })
    }



    private fun setUpTrackControls() {
        track_alpha.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) { if(p2) viewModel.trackAlpha.value = p1 }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        track_color_swatch.setOnClickListener{ showColorPicker(viewModel.trackColor) }
        viewModel.trackColor.observe(viewLifecycleOwner, Observer {
            track_color_swatch.imageTintList = ColorStateList.valueOf(it)
        })
    }


    //Generic function for setting up a spinner
    private fun <T> initSpinner(spinner: Spinner, field: MutableLiveData<T>, names: List<Any>, values: List<T>) {
        spinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, names)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                field.value = values[p2]
            }
        }
    }

    private fun showColorPicker(field: MutableLiveData<Int>, disableable: Boolean = false) {
        ColorPickerDialog.newInstance(field, disableable).show(parentFragmentManager, "ColorPicker")
    }

    companion object {
        val colors = listOf(
            Color.parseColor("#A431F6"),
            Color.parseColor("#03DAC5"),
            Color.parseColor("#0281E6"),
            Color.parseColor("#d50000"),
            Color.parseColor("#c51162"),
            Color.parseColor("#00c853"),
            Color.parseColor("#aeea00"),
            Color.parseColor("#ffab00"),
            Color.parseColor("#ff6d00"),
            Color.parseColor("#dd2c00"),
            Color.parseColor("#000000"),
            Color.parseColor("#ffffff")

            )
    }


}