package com.example.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.databinding.FragmentMainBinding
import com.google.gson.Gson
import java.util.*

class MainFragment : Fragment() {
    lateinit var binding : FragmentMainBinding

    private val viewModel : MainViewModel by activityViewModels()

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

        val adapter = context?.let{context ->  MainAdapter(context)}

        binding.mainRecView.layoutManager = LinearLayoutManager(context)
        binding.mainRecView.adapter = adapter
        binding.mainRecView.setHasFixedSize(true)

        viewModel.liveDataItemList.observe(this, androidx.lifecycle.Observer {
            adapter?.setData(it)
            Log.d("Kotlin", "데이터사이즈 - ${it.size}")
            Log.d("Kotlin - Main", "데이터 항목 - $${it.toString()}")
        })

        binding.mainAddlist.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddReminderFragment()
            findNavController().navigate(action)
        }

        binding.mainTodaybtn.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToTodayFragment()
            findNavController().navigate(action)
        }

        binding.mainImportantbtn.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToImportantFragment()
            findNavController().navigate(action)
        }

        binding.mainTimerbtn.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToTimerStopWatchFragment()
            findNavController().navigate(action)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return adapter!!.moveItem(viewHolder.adapterPosition, target.adapterPosition)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                return adapter!!.removeItem(viewHolder.adapterPosition)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if(actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    viewHolder?.itemView?.setBackgroundColor(
                        Color.LTGRAY
                    )
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(Color.WHITE)
            }
        }).attachToRecyclerView(binding.mainRecView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.mymenu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item1 -> {
                val action = MainFragmentDirections.actionMainFragmentToProfileFragment()
                findNavController().navigate(action)
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