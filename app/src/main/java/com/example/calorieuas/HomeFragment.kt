package com.example.calorieuas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager

class HomeFragment : Fragment() {

    lateinit var vSlider : ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        vSlider =view.findViewById(R.id.home_slider)

        val arraySlider = ArrayList<Int>()

        arraySlider.add(R.drawable.oatmeal)
        arraySlider.add(R.drawable.pancake)
        arraySlider.add(R.drawable.ramen)
        arraySlider.add(R.drawable.sanwidch)
        arraySlider.add(R.drawable.stroberi)
        arraySlider.add(R.drawable.supkari)//tambah item dan gambar

        val sliderAdapter = SliderAdapter(arraySlider, activity)
        vSlider.adapter = sliderAdapter

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}