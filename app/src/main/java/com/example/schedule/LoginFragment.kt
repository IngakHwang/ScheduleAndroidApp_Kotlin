package com.example.schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.schedule.databinding.FragmentLoginBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    val viewModel : MainViewModel by activityViewModels()
    var loadData = mutableListOf<MainData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)

        viewModel.removeAll()

        autoLogin()

        binding.loginJoinbtn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToJoinFragment()
            findNavController().navigate(action)
        }

        binding.loginLoginbtn.setOnClickListener {
            login()
        }
    }

    private fun autoLogin(){
        val shared = context?.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE)

        val autoID = shared?.getString("ID", null)
        val autoPW = shared?.getString("PW", null)
        if(autoID != null && autoPW != null){
            val bundle = bundleOf("inputID" to autoID)
            MainActivity.ID = autoID
            loadData()
            findNavController().navigate(R.id.mainFragment, bundle)
        }
    }

    private fun login(){
        val id = binding.loginInputID.text.toString().trim()
        val pw = binding.loginInputPW.text.toString()

        var checkID : String = ""
        var checkPW : String = ""

        val json = context?.getSharedPreferences("member",Context.MODE_PRIVATE)?.getString(id,"Fail")

        try{
            val jsonObject = JSONObject(json)
            checkID = jsonObject.getString("ID")
            checkPW = jsonObject.getString("PW")

        }catch (e: JSONException){e.printStackTrace()}

        when{
            id.isEmpty() -> {
                Toast.makeText(context,"아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.loginInputID.requestFocus()
            }
            pw.isEmpty() -> {
                Toast.makeText(context,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
                binding.loginInputPW.requestFocus()
            }
            id != checkID -> {
                Toast.makeText(context,"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show()
                binding.loginInputID.requestFocus()
            }
            pw != checkPW -> {
                Toast.makeText(context,"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()
                binding.loginInputPW.requestFocus()
            }

            else -> {
                context?.getSharedPreferences("AutoLogin",Context.MODE_PRIVATE)?.edit()?.run{
                    putString("ID",checkID)
                    putString("PW",checkPW)
                    commit()
                }
                Toast.makeText(context,"$checkID 님 환영합니다.", Toast.LENGTH_SHORT).show()
                val bundle = bundleOf("inputID" to checkID)
                MainActivity.ID = checkID
                loadData()
                findNavController().navigate(R.id.mainFragment, bundle)
            }
        }
    }

    private fun loadData(){
        val loadJson = context?.getSharedPreferences("${MainActivity.ID} reminder", Context.MODE_PRIVATE)?.getString(MainActivity.ID, null)

        Log.d("로그", "$loadJson")

        when {
            loadJson == null ->{ Log.d("로그", "Empty Data")}
            else -> {
                loadData  = Gson().fromJson(loadJson, object : TypeToken<MutableList<MainData>>(){}.type)

                viewModel.setItemList(loadData)

               Log.d("로그","Input Data")
            }
        }
    }

}