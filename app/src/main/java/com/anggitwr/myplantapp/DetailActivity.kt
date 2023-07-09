package com.anggitwr.myplantapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anggitwr.myplantapp.data.Tanaman
import com.anggitwr.myplantapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA = "extra"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = intent.getParcelableExtra<Tanaman>(EXTRA) as Tanaman
        binding.appCompatImageView.setImageResource(list.Images)
        binding.tvDetailNamatanaman.text =list.namaDaun
        binding.tvDetailnamalatin.text = list.namaLatin
        binding.tvInformasi1.text = list.manfaatDaun1
        binding.tvInformasi2.text = list.manfaatDaun2
    }
}