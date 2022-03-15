package com.example.schedule

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.schedule.databinding.FragmentJoinBinding
import com.google.gson.Gson

class JoinFragment : Fragment() {
    lateinit var binding : FragmentJoinBinding

    var checkedID : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentJoinBinding.bind(view)

        binding.joinCancelbtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.joinIdcheckbtn.setOnClickListener {
            checkID()
        }

        binding.joinCheckbtn.setOnClickListener {
            doJoin()
        }
    }

    fun checkID(){
        val joinID = binding.joinId.text.toString().trim()

        val savedText = context?.getSharedPreferences("member", MODE_PRIVATE)?.getString(joinID,"true")

        checkedID = if(savedText!="true"){
            AlertDialog.Builder(context).setMessage("중복된 아이디입니다.").setPositiveButton("확인",null).create().show()
            binding.joinId.requestFocus()
            false
        } else{
            Toast.makeText(context,"사용가능한 아이디입니다.",Toast.LENGTH_SHORT).show()
            true
        }
    }

    fun doJoin(){
        val joinId = binding.joinId.text.toString().trim()
        val joinPw = binding.joinPw.text.toString().trim()
        val joinPwCheck = binding.joinPwcheck.text.toString().trim()
        val joinMail = binding.joinMail.text.toString().trim()

        when{
            joinId.isEmpty() -> {
                Toast.makeText(context,"아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.joinId.requestFocus()
            }

            !checkedID -> {
                Toast.makeText(context,"아이디를 중복체크를 해주세요.", Toast.LENGTH_SHORT).show()
                binding.joinId.requestFocus()
            }

            joinPw.isEmpty() -> {
                Toast.makeText(context,"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.joinPw.requestFocus()
            }

            joinPw != joinPwCheck -> {
                Toast.makeText(context,"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                binding.joinPw.requestFocus()
            }

            joinMail.isEmpty() -> {
                Toast.makeText(context,"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.joinMail.requestFocus()
            }
            else -> {
                val changeJSON = Gson().toJson(Member(joinId,joinPw,joinMail))

                context?.getSharedPreferences("member", MODE_PRIVATE)?.edit()?.putString(joinId,changeJSON)?.commit()

                Toast.makeText(context,"회원가입되었습니다.",Toast.LENGTH_SHORT).show()

                findNavController().popBackStack()
            }
        }
    }
}