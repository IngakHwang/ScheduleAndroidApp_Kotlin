package com.example.schedule

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.schedule.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    lateinit var binding : FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        binding = FragmentMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainTextView.text = arguments?.getString("inputID")

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
                val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}