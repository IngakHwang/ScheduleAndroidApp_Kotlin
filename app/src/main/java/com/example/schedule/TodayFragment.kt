package com.example.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schedule.databinding.FragmentTodayBinding
import org.json.JSONException
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TodayFragment : Fragment() {
    private lateinit var binding : FragmentTodayBinding

    private val viewModel : MainViewModel by activityViewModels()

    val mainHander = MainHandler()

    var todayList = mutableListOf<MainData>()
    var notTodayList = mutableListOf<MainData>()

    val LOG = "Kotlin - TodayActivity"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodayBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdfYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val sdfMonth = SimpleDateFormat("M", Locale.getDefault())
        val sdfDay = SimpleDateFormat("d", Locale.getDefault())
        val currentTime = Calendar.getInstance().time

        val todayYear = sdfYear.format(currentTime)
        val todayMonth = sdfMonth.format(currentTime)
        val todayDay = sdfDay.format(currentTime)

        Log.d(LOG, "오늘날짜 - $todayYear/$todayMonth/$todayDay")

        val loadData = viewModel.getItemList()
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var i = 0
        while(i < loadData.size){
            val checkData = loadData[i]

            if(checkData.date.equals("$todayYear/$todayMonth/$todayDay")){
                todayList.add(checkData)

                val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val requestID : Int = System.currentTimeMillis().toInt()

                val receiverIntent = Intent(activity,AlarmReceiver::class.java)
                receiverIntent.putExtra("Title", checkData.title)
                receiverIntent.putExtra("Memo", checkData.memo)

                val pendingIntent = PendingIntent.getBroadcast(context,requestID,receiverIntent,PendingIntent.FLAG_MUTABLE)

                val dateFormat = SimpleDateFormat("yyyy/M/d HH:mm", Locale.getDefault())
                val from = "${checkData.date} ${checkData.time}"
                Log.d(LOG, "데이터 시간 - $from")

                var dateTime = Date()
                dateTime = dateFormat.parse(from)

                val calendar = Calendar.getInstance()
                calendar.time = dateTime

                Log.d(LOG, "예약 시간 - $dateTime")

                alarmManager.set(
                    AlarmManager.RTC,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            else {
                notTodayList.add(checkData)
            }

            i++
        }

        if(todayList.size <= 0){
            binding.todayNoitem.text = "오늘 할 일이 없습니다."
        } else {
            binding.todayNoitem.isVisible
        }

        val adapter = context?.let { SubAdapter(it) }

        Log.d(LOG, todayList.toString())
        Log.d(LOG, notTodayList.toString())

        adapter?.listData = todayList
        binding.todayRecView.adapter = adapter
        binding.todayRecView.layoutManager = LinearLayoutManager(context)
        binding.todayRecView.setHasFixedSize(true)

        BackgroundThread().start()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action -> {
                Toast.makeText(context,"저장", Toast.LENGTH_SHORT).show()
                if(todayList.size>=1){
                    var i = 0
                    while(i < notTodayList.size){
                        todayList.add(notTodayList[i])
                        i++
                    }
                    viewModel.setItemList(todayList)
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
            binding.todayImg.setImageResource(img)
            binding.todayImg.setOnClickListener {
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