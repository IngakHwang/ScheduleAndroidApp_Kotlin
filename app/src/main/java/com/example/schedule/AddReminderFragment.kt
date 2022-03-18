package com.example.schedule

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.schedule.databinding.FragmentAddReminderBinding
import java.util.*

class AddReminderFragment : Fragment() {
    lateinit var binding : FragmentAddReminderBinding
    val viewModel : MainViewModel by activityViewModels()

    var addImportant : String = ""
    var setDayTime : String = ""
    var setMyTime : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddReminderBinding.inflate(inflater, container , false)

        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addreminderSwitch.setOnCheckedChangeListener { compoundButton, b ->
            addImportant = when{
                b -> {
                    Toast.makeText(context,"중요한 일 등록",Toast.LENGTH_SHORT).show()
                    "중요"
                } else -> {
                    Toast.makeText(context,"중요한 일 해제",Toast.LENGTH_SHORT).show()
                    ""
                }
            }
        }

        binding.addreminderDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            TimePickerDialog(context, {
                    view, hourOfDay, mins ->

                setMyTime = ("$hourOfDay : $mins")
                binding.addreminderDate.setText("$setDayTime | $setMyTime")
            },hour,minute,true).show()

            DatePickerDialog(context!!, {
                view, years, months, dayOfMonth ->
                setDayTime = "$year / ${months+1} / ${dayOfMonth}"
            },year,month,day).show()
        }

        binding.addreminderCancelbtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addreminderCheckbtn.setOnClickListener {
            val list = MainData(
                binding.addreminderTitle.text.toString(),
                binding.addreminderMemo.text.toString(),
                binding.addreminderLocation.text.toString(),
                setDayTime,
                setMyTime,
                addImportant
            )
            viewModel.addItem(list)

            findNavController().popBackStack()

        }

    }
}