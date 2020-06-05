package com.owl93.determinateprogressdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.owl93.determinateprogressdemo.databinding.ActivityMainBinding
import com.owl93.dpb.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalArgumentException

private const val NUM_PAGES = 3

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    lateinit var pv: CircularProgressView
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        setContentView(binding.root)
        pv = progress_view

        setUpButtons()
        //General Options
        viewModel.direction.observe(this, Observer { pv.direction = it })
        viewModel.drawTrack.observe(this, Observer { pv.drawTrack = it })
        viewModel.trackAlpha.observe(this, Observer { pv.trackAlpha = it })
        viewModel.trackColor.observe(this, Observer { pv.trackColor = it })
        viewModel.trackSize.observe(this, Observer { pv.trackWidth = it.toFloat() })
        viewModel.strokeSize.observe(this, Observer { pv.strokeWidth = it.toFloat() })
        viewModel.currentProgress.observe(this, Observer {
            pv.progress = it
            animate_minus.isEnabled = it != 0f
            animate_plus.isEnabled = it != 100f
        })
        viewModel.maxValue.observe(this, Observer { pv.maxValue = it })
        viewModel.startingAngle.observe(this, Observer { pv.startingAngle = it })
        viewModel.animationInterpolator.observe(this, Observer { pv.animationInterpolator = it })
        viewModel.animationDuration.observe(this, Observer { pv.animationDuration = it })

        //Text Options
        viewModel.showText.observe(this, Observer { pv.textEnabled = it })
        viewModel.textSize.observe(this, Observer { pv.textSize = it.toFloat() })
        viewModel.textFormat.observe(this, Observer { pv.textFormat = it })
        viewModel.customText.observe(this, Observer { pv.text = it })

        //Style Options
        viewModel.strokeStyleMode.observe(this, Observer { updateStrokeStyle(it) })
        viewModel.strokeGradientStyle.observe(this, Observer { pv.strokeGradientStyle = it})
        viewModel.strokeGradientSize.observe(this, Observer { pv.strokeGradientSize = it })
        viewModel.strokeLinearGradientAngle.observe(this, Observer { pv.strokeGradientLinearAngle = it })
        viewModel.strokeGradientStartColor.observe(this, Observer {  pv.gradientStartColor = it})
        viewModel.strokeGradientCenterColor.observe(this, Observer {  pv.gradientCenterColor = it })
        viewModel.strokeGradientEndColor.observe(this, Observer {  pv.gradientEndColor = it })
        viewModel.strokeSolidColor.observe(this, Observer {  pv.strokeColor = it })

        viewModel.textStyleMode.observe(this, Observer { updateTextStyle(it) })
        viewModel.textGradientStyle.observe(this, Observer { pv.textGradientStyle = it })
        viewModel.textGradientSize.observe(this, Observer { pv.textGradientSize = it })
        viewModel.textLinearGradientAngle.observe(this, Observer { pv.textGradientLinearAngle = it })
        viewModel.textGradientStartColor.observe(this, Observer {  pv.textGradientStartColor = it})
        viewModel.textGradientCenterColor.observe(this, Observer {  pv.textGradientCenterColor = it })
        viewModel.textGradientEndColor.observe(this, Observer {  pv.textGradientEndColor = it })
        viewModel.textSolidColor.observe(this, Observer {  pv.textColor = it })

        initViewModelFields()

        viewPager = view_pager_container
        viewPager.adapter = PageAdapter(this)
        val tabs = listOf("General", "Text", "Style")
        TabLayoutMediator(pager_tab_layout, viewPager) {tab, position ->
            tab.text = tabs[position]
        }.attach()
        pv.animationListener  = object : DeterminateProgressViewListener {
            override fun onAnimationStart(from: Float, to: Float) {
                TransitionManager.beginDelayedTransition(progress_view_container,
                    AutoTransition().also { it.duration = 200 })
                animating_indicator.visibility = View.VISIBLE
            }

            override fun onAnimationEnd() {
                TransitionManager.beginDelayedTransition(progress_view_container,
                    AutoTransition().also { it.duration = 200 })
                animating_indicator.visibility = View.GONE
                viewModel.currentProgress.value = pv.progress

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.xml_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.xml_menu_item -> {
                val nameSpaces = mutableListOf("android", "android", "android")
                val attributes = mutableListOf("layout_width", "layout_height", "layout_margin")
                val values = mutableListOf("200dp", "200dp", "10dp")
                val xmlAttrs = dumpAttributes()
                for(pair in xmlAttrs) {
                    nameSpaces.add("app")
                    attributes.add(pair.first)
                    values.add(pair.second)
                }
                val bundle = Bundle().apply {
                    putStringArray("intent_namespaces", nameSpaces.toTypedArray())
                    putStringArray("intent_attributes", attributes.toTypedArray())
                    putStringArray("intent_values", values.toTypedArray())
                }
                startActivity(Intent(this, XmlActivity::class.java).also {
                    it.putExtras(bundle)
                })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateTextStyle(mode: MainActivityViewModel.StyleMode = viewModel.textStyleMode.value!!) {
        if(mode == MainActivityViewModel.StyleMode.GRADIENT) {
            pv.textGradientStartColor = viewModel.textGradientStartColor.value!!
            pv.textGradientCenterColor = viewModel.textGradientCenterColor.value!!
            pv.textGradientEndColor = viewModel.textGradientEndColor.value!!
            pv.textGradientStyle = viewModel.textGradientStyle.value!!
            pv.textGradientLinearAngle = viewModel.textLinearGradientAngle.value!!
            pv.textGradientSize = viewModel.textGradientSize.value!!
        }else {
            pv.textColor = viewModel.textSolidColor.value!!
            pv.textGradientStartColor = 0
            pv.textGradientCenterColor = 0
            pv.textGradientEndColor = 0
        }
    }

    private fun updateStrokeStyle(mode: MainActivityViewModel.StyleMode = viewModel.strokeStyleMode.value!!) {
        if(mode == MainActivityViewModel.StyleMode.GRADIENT) {
            pv.gradientStartColor = viewModel.strokeGradientStartColor.value!!
            pv.gradientCenterColor = viewModel.strokeGradientCenterColor.value!!
            pv.gradientEndColor = viewModel.strokeGradientEndColor.value!!
            pv.strokeGradientStyle = viewModel.strokeGradientStyle.value!!
            pv.strokeGradientSize = viewModel.strokeGradientSize.value!!
            pv.strokeGradientLinearAngle = viewModel.strokeLinearGradientAngle.value!!
        }else {
            pv.strokeColor = viewModel.strokeSolidColor.value!!
            pv.gradientStartColor = 0
            pv.gradientCenterColor = 0
            pv.gradientEndColor = 0
        }
    }

    private fun initViewModelFields() {

        viewModel.strokeSize.value = pv.strokeWidth
        viewModel.textSize.value = pv.textSize
        viewModel.startingAngle.value = pv.startingAngle
        viewModel.maxValue.value = pv.maxValue
        viewModel.currentProgress.value = pv.progress
        viewModel.textFormat.value = pv.textFormat

        viewModel.strokeStyleMode.value = if(pv.gradientStartColor != -1 && pv.gradientEndColor != -1)
            MainActivityViewModel.StyleMode.GRADIENT else MainActivityViewModel.StyleMode.SOLID
        viewModel.strokeGradientStyle.value = pv.strokeGradientStyle
        viewModel.strokeSolidColor.value = pv.strokeColor
        viewModel.strokeGradientStartColor.value = pv.gradientStartColor
        viewModel.strokeGradientCenterColor.value = pv.gradientCenterColor
        viewModel.strokeGradientEndColor.value = pv.gradientEndColor
        viewModel.strokeGradientSize.value = pv.strokeGradientSize
        viewModel.strokeLinearGradientAngle.value = pv.strokeGradientLinearAngle

        viewModel.textStyleMode.value = if(pv.textGradientStartColor != -1 && pv.textGradientEndColor != -1)
            MainActivityViewModel.StyleMode.GRADIENT else MainActivityViewModel.StyleMode.SOLID
        viewModel.textGradientStyle.value = pv.textGradientStyle
        viewModel.textSolidColor.value = pv.textColor
        viewModel.textGradientStartColor.value = pv.textGradientStartColor
        viewModel.textGradientCenterColor.value = pv.textGradientCenterColor
        viewModel.textGradientEndColor.value = pv.textGradientEndColor
        viewModel.textGradientSize.value = pv.textGradientSize
        viewModel.textLinearGradientAngle.value = pv.textGradientLinearAngle

        viewModel.trackSize.value = pv.trackWidth
        viewModel.trackColor.value = pv.trackColor
        viewModel.trackAlpha.value = if(pv.trackAlpha == -1)
            CircularProgressView.DEFAULT_TRACK_ALPHA else pv.trackAlpha
    }

    private inner class PageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount() = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> GeneralOptionsFragment()
                1 -> TextOptionsFragment()
                2 -> StyleOptionsFragment()
                else -> throw IllegalArgumentException("can't find fragment for pager position $position")
            }
        }
    }

    private fun setUpButtons() {
        //animators
        animate_minus.setOnClickListener {
            val current = progress_view.progress
            val tenPercent = progress_view.maxValue * .1f
            val amt = if (current - tenPercent >= 0) current - tenPercent else 0f
            progress_view.animateProgressChange(amt)

        }
        animate_plus.setOnClickListener {
            val current = progress_view.progress
            val tenPercent = progress_view.maxValue * .1f
            val amt = if (current + tenPercent <= progress_view.maxValue) current + tenPercent else progress_view.maxValue
            progress_view.animateProgressChange(amt)
        }
    }

    private fun dumpAttributes(): List<Pair<String, String>> {
        val density = resources.displayMetrics.density
        val gradientStyles = mapOf(Gradient.STYLE_SWEEP to "sweep", Gradient.STYLE_LINEAR to "linear", Gradient.STYLE_RADIAL to "radial")
        val textFormats = mapOf(TextFormat.PERCENT to "pcnt", TextFormat.DECIMAL_PERCENT to "pcnt_decimal", TextFormat.INT to "integer", TextFormat.FLOAT to "progress")

        val list = mutableListOf<Pair<String, String>>(
            Pair("maxValue", pv.maxValue.toString()),
            Pair("strokeWidth", "${pv.strokeWidth/density}dp"),
            Pair("strokeColor", pv.strokeColor.toColorString()),
            Pair("progress", pv.progress.toString()),
            Pair("direction", if(pv.direction == Direction.CCW) "ccw" else "cw"),
            Pair("startingAngle", pv.startingAngle.toString()),
            Pair("drawTrack", pv.drawTrack.toString()),
            Pair("trackColor", pv.trackColor.toColorString()),
            Pair("trackWidth", "${pv.trackWidth/density}dp"),
            Pair("trackAlpha", pv.trackAlpha.toString()),
            Pair("gradientStartColor", pv.gradientStartColor.toColorString()),
            Pair("gradientCenterColor", pv.gradientCenterColor.toColorString()),
            Pair("gradientEndColor", pv.gradientEndColor.toColorString()),
            Pair("strokeGradientStyle", gradientStyles.getOrElse(pv.strokeGradientStyle, {"sweep"})),
            Pair("strokeGradientSize", if(pv.strokeGradientSize == StrokeGradient.STROKE_ONLY) "stroke_only" else "view"),
            Pair("strokeGradient_linearAngle", pv.strokeGradientLinearAngle.toString()),
            Pair("textEnabled", pv.textEnabled.toString()),
            Pair("textSize", "${pv.textSize/density}sp"),
            Pair("textFormat", textFormats.getOrElse(pv.textFormat, {"pcnt"})),
            Pair("textColor", pv.textColor.toColorString()),
            Pair("textGradientStartColor", pv.textGradientStartColor.toColorString()),
            Pair("textGradientCenterColor", pv.textGradientCenterColor.toColorString()),
            Pair("textGradientEndColor", pv.textGradientEndColor.toColorString()),
            Pair("textGradientStyle", gradientStyles.getOrElse(pv.textGradientStyle, {"sweep"})),
            Pair("textGradient_linearAngle", pv.textGradientLinearAngle.toString()),
            Pair("textGradientSize", if(pv.textGradientSize == TextGradient.TEXT_ONLY) "text_only" else "View" ),
            Pair("progressAnimationDuration", pv.animationDuration.toString()),
            Pair("animationInterpolator", when(pv.animationInterpolator) {
                is DecelerateInterpolator -> "decelerate"
                is AccelerateInterpolator -> "accelerate"
                is AccelerateDecelerateInterpolator -> "accelerate_decelerate"
                is LinearInterpolator -> "linear"
                is AnticipateInterpolator -> "anticipate"
                is OvershootInterpolator -> "overshoot"
                is AnticipateOvershootInterpolator -> "anticipate_overshoot"
                else -> "linear"
            })
        )

        if(!pv.text.isNullOrBlank()) list.add(Pair("text", pv.text!!))
        return list
    }


    companion object {
        private const val TAG = "MainActivity"
        val attributes = listOf(
            "maxValue",
            "strokeWidth",
            "strokeColor",
            "direction",
            "progress",
            "startingAngle",
            "drawTrack",
            "trackColor",
            "trackWidth",
            "trackAlpha",
            "gradientStartColor",
            "gradientCenterColor",
            "gradientEndColor",
            "strokeGradientStyle",
            "strokeGradient_linearAngle",
            "strokeGradientSize",
            "textEnabled",
            "textSize",
            "textFormat",
            "textColor",
            "textGradientStartColor",
            "textGradientCenterColor",
            "textGradientEndColor",
            "textGradientStyle",
            "textGradient_linearAngle",
            "textGradientSize",
            "progressAnimationDuration",
            "animationInterpolator",
            "text"
        )
    }
}

class MainActivityViewModel : ViewModel() {

    enum class StyleMode {
        SOLID,
        GRADIENT
    }

    val gradientStyleNames = listOf("Sweep", "Linear", "Radial", "Candy Cane")
    val gradientStyles = listOf(Gradient.STYLE_SWEEP, Gradient.STYLE_LINEAR, Gradient.STYLE_RADIAL, Gradient.STYLE_CANDY_CANE)

    val textGradientStyleNames = listOf("Sweep", "Linear", "Radial")
    val textGradientStyles = listOf(Gradient.STYLE_SWEEP, Gradient.STYLE_LINEAR, Gradient.STYLE_RADIAL)
    val interpolatorNames = listOf(
        "Decelerate",
        "Accelerate",
        "Linear",
        "Accel. & Decel.",
        "Anticipate",
        "Overshoot",
        "Antic. & Overshoot"
    )
    val interpolators = listOf(
        DecelerateInterpolator(),
        AccelerateInterpolator(),
        LinearInterpolator(),
        AccelerateDecelerateInterpolator(),
        AnticipateInterpolator(),
        OvershootInterpolator(),
        AnticipateOvershootInterpolator()
    )

    val linearAngles = listOf(0, 45, 90, 135, 180, 225, 270, 315)

    val formatOptions = listOf("Percent", "Decimal Percent", "Integer", "Progress")

    val customText: MutableLiveData<String?> = MutableLiveData(null)
    val showText: MutableLiveData<Boolean> = MutableLiveData(true)
    val currentProgress: MutableLiveData<Float> = MutableLiveData(65f)
    val maxValue: MutableLiveData<Float> = MutableLiveData(100f)

    val strokeSize: MutableLiveData<Float> = MutableLiveData(50f)
    val drawTrack: MutableLiveData<Boolean> = MutableLiveData(true)
    val trackSize: MutableLiveData<Float> = MutableLiveData(50f)
    val textSize: MutableLiveData<Float> = MutableLiveData(80f)
    val direction: MutableLiveData<Direction> = MutableLiveData(Direction.CW)

    val textFormat: MutableLiveData<TextFormat> = MutableLiveData(TextFormat.INT)
    val startingAngle: MutableLiveData<Int> = MutableLiveData(0)

    val animationDuration = MutableLiveData(600L)

    val animationInterpolator = MutableLiveData(interpolators[0])



    //style attrs
    val strokeStyleMode = MutableLiveData(StyleMode.GRADIENT)
    val textStyleMode = MutableLiveData(StyleMode.GRADIENT)

    val strokeGradientStyle = MutableLiveData(Gradient.STYLE_SWEEP)
    val textGradientStyle = MutableLiveData(Gradient.STYLE_SWEEP)

    val strokeGradientSize = MutableLiveData(StrokeGradient.STROKE_ONLY)
    val textGradientSize = MutableLiveData(TextGradient.TEXT_ONLY)

    val textLinearGradientAngle = MutableLiveData(0)
    val strokeLinearGradientAngle: MutableLiveData<Int> = MutableLiveData(0)

    val strokeSolidColor = MutableLiveData(0)

    val strokeGradientStartColor = MutableLiveData(0)
    val strokeGradientCenterColor = MutableLiveData(0)
    val strokeGradientEndColor = MutableLiveData(0)
    val textSolidColor = MutableLiveData(0)

    val textGradientStartColor = MutableLiveData(0)
    val textGradientCenterColor = MutableLiveData(0)
    val textGradientEndColor = MutableLiveData(0)


    val trackAlpha: MutableLiveData<Int> = MutableLiveData(CircularProgressView.DEFAULT_TRACK_ALPHA)
    val trackColor: MutableLiveData<Int> = MutableLiveData(-1)


}