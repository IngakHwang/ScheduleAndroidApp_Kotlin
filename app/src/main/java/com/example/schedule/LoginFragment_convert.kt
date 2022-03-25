package com.example.schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.schedule.databinding.FragmentLoginBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class LoginFragment_convert : Fragment() {
    var _binding:FragmentLoginBinding? = null
    val binding:FragmentLoginBinding
        get() = _binding ?: throw IllegalStateException()

    val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_login, container, false).apply {
        _binding = FragmentLoginBinding.bind(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.removeAll()

        autoLogin()

        loginJoinbtn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToJoinFragment()
            findNavController().navigate(action)
        }

        loginLoginbtn.setOnClickListener {
            login()
        }
    }

    private fun autoLogin(){
        val shared = requireContext().getPreference("AutoLogin")
        shared.run {
            getString("ID",null) to getString("PW",null)
        }.let { (autoID, autoPW) ->
            autoID.takeUnless {
                it.isNullOrEmpty() && autoPW.isNullOrEmpty()
            }?.let { autoId ->
                // 조건이 맞는 경우
                moveMainFragment(autoId)
            }
        }
    }

    private fun login() = with(binding) {
        val id = loginInputID.text.toString().trim()
        val pw = loginInputPW.text.toString()

        requireContext().getPreference("member")
            .getString(id,"Fail")
            ?.let { memberId ->
                JSONObject(memberId)
            }
            ?.run {
                getString("ID") to getString("PW")
            }
            ?.let { (prefAutoId, prefAutoPw) ->
                when{
                    id.isEmpty() or (id != prefAutoId) -> {
                        showToast("아이디를 입력해주세요.")
                        loginInputID.requestFocus()
                    }
                    pw.isEmpty() || (pw != prefAutoPw) -> {
                        showToast("비밀번호를 입력해주세요.")
                        loginInputPW.requestFocus()
                    }
                    else -> {
                        context?.getSharedPreferences("AutoLogin",Context.MODE_PRIVATE)?.edit()?.run{
                            putString("ID",prefAutoId)
                            putString("PW",prefAutoPw)
                            commit()
                        }
                        showToast("$prefAutoId 님 환영합니다.")
                        moveMainFragment(prefAutoId)
                    }
                }
            }
    }

    private fun moveMainFragment(autoId:String) {
        MainActivity.ID = autoId
        loadData()
        findNavController().navigate(
            R.id.mainFragment,
            bundleOf("inputID" to autoId)
        )
    }

    private fun loadData(){
        requireContext().getPreference("${MainActivity.ID} reminder")
            .getString(MainActivity.ID, null)
            ?.let { loadJson ->
                Gson().fromJson<MutableList<MainData>>(
                    loadJson,
                    object : TypeToken<MutableList<MainData>>(){}.type
                ).let { loadData ->
                    viewModel.setItemList(loadData)
                }
            }
            ?: run {
                Log.d("로그", "Empty Data")
            }
    }

}