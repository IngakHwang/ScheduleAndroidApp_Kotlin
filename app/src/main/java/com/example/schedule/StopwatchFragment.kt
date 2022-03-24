package com.example.schedule

import android.annotation.SuppressLint
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schedule.databinding.FragmentStopwatchBinding

class StopwatchFragment : Fragment() {

    lateinit var binding : FragmentStopwatchBinding

    lateinit var timeThread : Thread
    var isRunning = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStopwatchBinding.inflate(inflater,container,false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.stopwatchStartbtn.setOnClickListener {
            binding.stopwatchStartbtn.visibility = View.GONE

            binding.stopwatchRecordbtn.visibility = View.VISIBLE
            binding.stopwatchPausebtn.visibility = View.VISIBLE
            binding.stopwatchStopbtn.visibility = View.VISIBLE

            timeThread = Thread(TimeThread())
            timeThread.start()
        }

        binding.stopwatchStopbtn.setOnClickListener {
            binding.stopwatchRecordbtn.visibility = View.GONE
            binding.stopwatchPausebtn.visibility = View.GONE
            binding.stopwatchStopbtn.visibility = View.GONE

            binding.stopwatchStartbtn.visibility = View.VISIBLE

            timeThread.interrupt()

            binding.stopwatchRecordView.text = ""
            binding.stopwatchTimeView.text = "00:00:00:00"

        }

        binding.stopwatchPausebtn.setOnClickListener {
            isRunning = !isRunning
            if(isRunning){
                binding.stopwatchPausebtn.text = "일시정지"
            } else{
                binding.stopwatchPausebtn.text = "시작"
            }
        }

        binding.stopwatchRecordbtn.setOnClickListener {
            binding.stopwatchRecordView.text = "${binding.stopwatchRecordView.text} ${binding.stopwatchTimeView.text} \n"
        }

    }
    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            val mSec = msg.arg1 % 100
            val sec = (msg.arg1 / 100) % 60
            val min = (msg.arg1 / 100) / 60
            val hour = (msg.arg1 / 100) / 360

            val result : String = String.format("%02d:%02d:%02d:%02d", hour,min,sec,mSec)

            binding.stopwatchTimeView.text = result
        }
    }

    inner class TimeThread : Runnable{
        override fun run() {

            var i = 0
            while(true){
                while(isRunning){
                    val msg = Message()
                    msg.arg1 = i++
                    handler.sendMessage(msg)

                    try{
                        Thread.sleep(10)
                    }catch (e: InterruptedException){
                        e.printStackTrace()
                        return
                    }

                }
            }
        }
    }
}