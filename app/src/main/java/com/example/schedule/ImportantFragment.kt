package com.example.schedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schedule.databinding.FragmentImportantBinding
import java.lang.Exception


class ImportantFragment : Fragment() {
    lateinit var binding : FragmentImportantBinding

    val mainHander = MainHandler()

    private val viewModel : MainViewModel by activityViewModels()

    var importantList = mutableListOf<MainData>()
    var notImportantList = mutableListOf<MainData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportantBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadData = viewModel.getItemList()

        var i = 0
        while(i < loadData.size){
            val checkData = loadData[i]

            if(checkData.important.equals("중요")){
                importantList.add(checkData)
            }else{
                notImportantList.add(checkData)
            }

            i++
        }

        if(importantList.size <= 0){
            binding.importantNoitem.text = "중요한 일이 없습니다."
        } else {
            binding.importantNoitem.isVisible
        }

        val adapter = context?.let{SubAdapter(it)}

        adapter?.listData = importantList
        binding.importantRecView.adapter = adapter
        binding.importantRecView.layoutManager = LinearLayoutManager(context)
        binding.importantRecView.setHasFixedSize(true)

        BackgroundThread().start()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action -> {
                Toast.makeText(context,"저장",Toast.LENGTH_SHORT).show()
                if(importantList.size>=1){
                    var i = 0
                    while(i < notImportantList.size){
                        importantList.add(notImportantList[i])
                        i++
                    }
                    viewModel.setItemList(importantList)
                }
                findNavController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    inner class MainHandler : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val img = msg.data.getInt("img")
            val uri = msg.data.getString("uri")
            binding.importantImg.setImageResource(img)
            binding.importantImg.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
            }
        }
    }

    inner class BackgroundThread : Thread(){
        var value = 0
        var img = 0
        var uri = ""
        override fun run() {
            while(true){
                value++
                when(value){
                    1->{
                        img = R.drawable.google2
                        uri = "https://www.google.com"
                    }
                    2->{
                        img = R.drawable.naver2
                        uri = "https://m.naver.com"
                    }
                    3->{
                        img = R.drawable.kakao2
                        uri = "https://m.daum.net"
                        value=0
                    }
                }
                val bundle = Bundle()
                bundle.putInt("img",img)
                bundle.putString("uri", uri)

                val message = mainHander.obtainMessage()
                message.data=bundle

                mainHander.sendMessage(message)

                try{
                    Thread.sleep(3000)
                }catch (e: Exception){}
            }
        }
    }
}