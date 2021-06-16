package com.github.customviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.customviews.databinding.ActivityQqStepViewBinding

class QQStepViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQqStepViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQqStepViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.qqStepView.setMaxStep(8000)
        binding.qqStepView.setCurrentStepSmoothly(6000)

//        val a = ObjectAnimator.ofInt(0, 6000)
//        a.duration = 1000
//        a.interpolator = DecelerateInterpolator()
//        a.addUpdateListener {
//            val value = it.animatedValue as Int
//            binding.qqStepView.setCurrentStep(value)
//        }
//        a.start()
    }
}