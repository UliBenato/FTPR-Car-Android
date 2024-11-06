package com.example.myapitest.database.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapitest.R
import com.example.myapitest.model.Car
import com.example.myapitest.ui.loadUrl

class CarAdapter (
    private val cars: List<Car>,
    private val carClickListener: (Car) -> Unit
    ): RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

        class CarViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val imageView: ImageView = view.findViewById(R.id.image)
            val modelTextView: TextView = view.findViewById(R.id.model)
            val yearTextView: TextView = view.findViewById(R.id.year)
            val licenceTextView: TextView = view.findViewById(R.id.license)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_car_layout, parent, false)
            return CarViewHolder(view)
        }

        override fun getItemCount(): Int = cars.size

        override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
            val item = cars[position]
            holder.itemView.setOnClickListener{
                carClickListener.invoke(item)
            }
            holder.modelTextView.text = "${item.value.name}"
            holder.yearTextView.text = holder.itemView.context.getString(R.string.year, item.value.year.toString())
            holder.licenceTextView.text = item.value.licence
            holder.imageView.loadUrl(item.value.imageUrl)
        }
}