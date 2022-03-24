package com.example.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.schedule.databinding.FragmentTimerStopWatchBinding

class TimerStopWatchFragment : Fragment() {
    lateinit var binding : FragmentTimerStopWatchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTimerStopWatchBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timerStopwatch.setOnClickListener {
            val stopWatchFrag = StopwatchFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.timer_frame,stopWatchFrag)
                .commit()
        }

        binding.timerTimer.setOnClickListener {
            val timerFrag = TimerFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.timer_frame,timerFrag)
                .commit()
        }
    }
}