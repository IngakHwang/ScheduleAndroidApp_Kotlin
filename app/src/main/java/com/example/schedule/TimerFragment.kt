package com.example.schedule

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schedule.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    lateinit var binding : FragmentTimerBinding

    var time : Long = 0
    var tempTime : Long = 0

    var firstState = true
    var timerRunning = false

    lateinit var countDownTimer : CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimerBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timerStartbtn.setOnClickListener {
            firstState = true

            binding.timerSettinglayout.visibility = View.GONE
            binding.timerStartlayout.visibility = View.VISIBLE

            startStop()
        }

        binding.timerStopbtn.setOnClickListener {
            startStop()
        }

        binding.timerCancelbtn.setOnClickListener {
            binding.timerSettinglayout.visibility = View.VISIBLE
            binding.timerStartlayout.visibility = View.GONE
            firstState = true
            stopTimer()
        }

        updateTimer()

    }

    private fun startStop(){
        if(timerRunning) {stopTimer()}
        else {startTimer()}
    }

    private fun startTimer(){

        time = when{
            firstState -> {
                val min = binding.timerMinuteIP.text.toString()
                val second = binding.timerSecondIP.text.toString()
                min.toLong() * 60000 + second.toLong() * 1000 + 1000
            } else ->{
                tempTime
            }
        }

        countDownTimer = object : CountDownTimer(time, 1000){
            override fun onTick(p0: Long) {
                tempTime = p0
                updateTimer()
            }

            override fun onFinish() {}
        }.start()

        binding.timerStopbtn.text = "일시정지"
        timerRunning = true
        firstState = false
    }

    private fun stopTimer(){
        countDownTimer.cancel()
        timerRunning = false
        binding.timerStopbtn.text = "계속"
    }

    private fun updateTimer() {
        val minute : Int = (tempTime % 3600000 / 60000).toInt()
        val second : Int = (tempTime % 60000 / 1000).toInt()

        var timeLeftText = ""
        if(minute < 10) timeLeftText += "0"
        timeLeftText += "$minute:"

        if(second < 10) timeLeftText += "0"
        timeLeftText += second

        binding.timerView.text=timeLeftText

        if(timeLeftText == "00:00" && timerRunning){
            binding.timerSettinglayout.visibility = View.VISIBLE
            binding.timerStartlayout.visibility = View.GONE
            firstState = true
            stopTimer()

            val builder = AlertDialog.Builder(context)

            builder.setTitle("타이머").setMessage("타이머가 종료되었습니다.")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->

            })

            builder.create().show()
        }
    }
}