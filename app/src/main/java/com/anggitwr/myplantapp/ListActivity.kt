package com.anggitwr.myplantapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggitwr.myplantapp.adapter.TanamanAdapter
import com.anggitwr.myplantapp.data.Tanaman


class ListActivity : AppCompatActivity() {

    private val list = ArrayList<Tanaman>()

    private lateinit var pindah : Button

    private lateinit var recV : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recV = findViewById(R.id.rv_list)
        recV.setHasFixedSize(true)

        pindah = findViewById(R.id.btn_scan)

        list.addAll(listTanaman)
        showRecyclerList()
        action()
    }

    private val listTanaman: ArrayList<Tanaman>
        get() {
            val dataImages =resources.obtainTypedArray(R.array.imag)
            val dataNamaDaun = resources.getStringArray(R.array.nama_daun)
            val dataNamLatin = resources.getStringArray(R.array.nama_latin)
            val dataManfaat1 = resources.getStringArray(R.array.manfaat_daun_1)
            val dataManfaat2 = resources.getStringArray(R.array.manfaat_daun_2)

            val listTanaman = ArrayList<Tanaman>()
            for (i in dataNamaDaun.indices){
                val tanaman = Tanaman(
                    dataNamaDaun[i],
                    dataNamLatin[i],
                    dataImages.getResourceId(i, -1),
                    dataManfaat1[i],
                    dataManfaat2[i]
                )
                listTanaman.add(tanaman)
            }
            return listTanaman
        }

    private fun showRecyclerList(){
        recV.layoutManager = LinearLayoutManager(this)
        val TanamanAdapter = TanamanAdapter(list)
        recV.adapter = TanamanAdapter

        TanamanAdapter.setOnItemClickCallback(object : TanamanAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Tanaman) {
                val moveWithData = Intent (this@ListActivity, DetailActivity::class.java)
                moveWithData.putExtra(DetailActivity.EXTRA, data)
                startActivity(moveWithData)
            }
        })
    }
    private fun action(){
        pindah.setOnClickListener {
            val intent = Intent(this@ListActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }

}