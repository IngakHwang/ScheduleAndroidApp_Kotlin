package com.example.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.databinding.FragmentMainBinding
import com.google.gson.Gson
import java.util.*

class MainFragment : Fragment() {
    lateinit var binding : FragmentMainBinding

    val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        binding = FragmentMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Kotlin", "ID - ${MainActivity.ID}")

        val adapter = context?.let{MainAdapter(it)}

        binding.mainRecView.layoutManager = LinearLayoutManager(context)
        binding.mainRecView.adapter = adapter
        binding.mainRecView.setHasFixedSize(true)

        viewModel.liveDataItemList.observe(this, androidx.lifecycle.Observer {
            adapter?.setData(it)
            Log.d("Kotlin", "데이터사이즈 - ${it.size}")
        })

        binding.mainAddlist.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddReminderFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.mymenu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item1 -> {
                Toast.makeText(context,"프로필 대기 중",Toast.LENGTH_SHORT).show()
            }
            R.id.menu_item2 -> {
                Toast.makeText(context,"로그아웃", Toast.LENGTH_SHORT).show()
                context?.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE)?.edit()?.run {
                    clear()
                    apply()
                }
                MainActivity.ID = ""
                val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        val gson = Gson().toJson(viewModel.getItemList())
        context?.getSharedPreferences("${MainActivity.ID} reminder", Context.MODE_PRIVATE)
            ?.edit()?.run {
                putString(MainActivity.ID, gson)
                apply()
            }
    }
}