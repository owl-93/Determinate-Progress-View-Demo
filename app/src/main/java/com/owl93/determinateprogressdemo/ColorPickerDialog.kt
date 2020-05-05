package com.owl93.determinateprogressdemo

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.color_picker_dialog.view.*
import kotlinx.android.synthetic.main.color_picker_dialog.view.off_checkbox


fun Int.toColorString(): String {
    return String.format("#%08X", 0xFFFFFFFF and this.toLong())
}
class ColorPickerDialog : DialogFragment() {
    lateinit var field: MutableLiveData<Int>
    lateinit var rootView: View
    private var currentColor: Int = -1
    private var disableable = false
    private var red: Int = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        rootView = requireActivity().layoutInflater.inflate(R.layout.color_picker_dialog, null, false)
        red = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
        return activity?.let {
            val builder =
                AlertDialog.Builder(it, R.style.Theme_MaterialComponents_DayNight_Dialog_Alert)
                    .apply {
                        setMessage("Edit Color")
                        setView(rootView)
                        setPositiveButton(R.string.ok) { _, _ -> field.value = currentColor }
                        setNegativeButton(R.string.cancel) { _, _ -> }
                    }
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }


    override fun onResume() {
        super.onResume()
        updatePreview()
        rootView.off_checkbox.apply {
            visibility = if(disableable) View.VISIBLE else View.GONE
            isChecked = currentColor == Color.TRANSPARENT && disableable
            setOnCheckedChangeListener { _, enabled ->
                if (enabled)  currentColor = Color.TRANSPARENT
                updatePreview()
            }
        }

        for ((idx, swatch) in rootView.color_swatch_row_1.children.withIndex()) swatch.setOnClickListener {
            currentColor = StyleOptionsFragment.colors[idx]
            rootView.off_checkbox?.isChecked = false
            updatePreview()
        }
        for ((idx, swatch) in rootView.color_swatch_row_2.children.withIndex()) swatch.setOnClickListener {
            currentColor = StyleOptionsFragment.colors[idx + rootView.color_swatch_row_1.childCount]
            rootView.off_checkbox?.isChecked = false
            updatePreview()
        }

        rootView.current_color_hex_code.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var newColor: Int = Color.WHITE
                try {
                    newColor = Color.parseColor(p0.toString())
                    rootView.current_color_hex_code.error = null
                } catch (ex: Exception) {
                    rootView.current_color_hex_code.error = "invalid color"
                } finally {
                    currentColor = newColor
                    updatePreview(false)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    private fun updatePreview(updateTextField: Boolean = true) {
        rootView.apply {
            preview_swatch.apply {
                setImageResource(if(currentColor == Color.TRANSPARENT) R.drawable.ic_no_color else R.drawable.color_swatch)
                imageTintList = ColorStateList.valueOf(if(currentColor == Color.TRANSPARENT) red else currentColor)
            }
            if (updateTextField) {
                val color = Color.argb(Color.alpha(currentColor), Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor)).toLong()
                current_color_hex_code.editText?.setText(String.format("#%08X", 0xFFFFFFFF and color))
                current_color_hex_code.editText?.isEnabled =  !(off_checkbox.isChecked && disableable)
            }
        }

    }

    companion object {
        fun newInstance(
            field: MutableLiveData<Int>,
            disableable: Boolean = false
        ): ColorPickerDialog {
            return ColorPickerDialog().also {
                it.field = field
                it.currentColor = field.value!!
                it.disableable = disableable
            }
        }
    }
}