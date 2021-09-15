package com.alvin.linkpreview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alvin.linkpreview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SharingLinkPreview.PreviewListener {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            preView.setListener(this@MainActivity)

            btnShow.setOnClickListener {
                if (etLink.text.isNullOrEmpty()) {
                    showMsg("Please enter the link here")
                } else {
                    preView.setData(etLink.text.toString())
                }
            }
        }
    }

    override fun onDataReady(preview: SharingLinkPreview?) {
        binding.apply {
            preView.setMessage(preview?.link)
            preView.showLayout(preview?.link)
            when {
                preview?.link.isNullOrEmpty() -> {
                    showMsg("Enter valid link here")
                }
                preview?.link == "null" -> {
                    showMsg("Enter valid link here")
                }
            }
        }
    }

    override fun onLinkClicked(link: String?) {
        showMsg(link)
    }

    private fun showMsg(msg: String?) {
        Toast.makeText(this@MainActivity, "$msg", Toast.LENGTH_SHORT).show()
    }
}