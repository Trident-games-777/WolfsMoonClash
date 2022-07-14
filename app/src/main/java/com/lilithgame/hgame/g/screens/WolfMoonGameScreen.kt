package com.lilithgame.hgame.g.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lilithgame.hgame.g.R
import com.lilithgame.hgame.g.anim.WolfMoonAnim
import com.lilithgame.hgame.g.databinding.ScreenWolfMoonGameBinding

class WolfMoonGameScreen : AppCompatActivity() {
    private var credits = 1000
    private val animation = WolfMoonAnim()
    private lateinit var binding: ScreenWolfMoonGameBinding
    private val items = listOf(R.drawable.a, R.drawable.k, R.drawable.wolf, R.drawable.bison)
    private val current = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenWolfMoonGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNext.isEnabled = false
        credits -= 50
        binding.credits.text = credits.toString()
        setCurrent()
        animation.spreadIn(
            topLeft = binding.iv1,
            topRight = binding.iv2,
            bottomLeft = binding.iv3,
            bottomRight = binding.iv4
        ) {
            checkPrise()
            binding.buttonNext.isEnabled = true
        }

        binding.buttonNext.setOnClickListener {
            credits -= 50
            binding.credits.text = credits.toString()
            it.isEnabled = false
            animation.spreadOut(
                topLeft = binding.iv1,
                topRight = binding.iv2,
                bottomLeft = binding.iv3,
                bottomRight = binding.iv4,
                onMiddle = { setCurrent() }
            ) {
                checkPrise()
                it.isEnabled = true
            }
        }
    }

    private fun setCurrent() {
        current.clear()
        repeat(4) { current.add(items.shuffled().first()) }
        binding.iv1.setImageResource(current[0])
        binding.iv2.setImageResource(current[1])
        binding.iv3.setImageResource(current[2])
        binding.iv4.setImageResource(current[3])
    }

    private fun checkPrise() {
        val wolfs = current.count { it == R.drawable.wolf }
        val bosons = current.count { it == R.drawable.bison }
        if (wolfs == 3) {
            credits += 150
        } else if (wolfs == 2) {
            credits += 75
        }
        if (bosons == 3) {
            credits += 100
        } else if (bosons == 2) {
            credits += 50
        }
        binding.credits.text = credits.toString()
    }
}