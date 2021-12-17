package com.example.fooddelivery.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.fooddelivery.MyCart
import com.example.fooddelivery.R
import com.example.fooddelivery.database.RestaurantEntity
import com.example.fooddelivery.model.RestaurantMenu


class RestaurantMenuAdapter(val context:Context,val restaurantId:String,val restaurantName:String,val proceedToCartLayout:
RelativeLayout,val buttonProceedToCart:Button,val restaurantMenu:ArrayList<RestaurantMenu>):RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {


    var itemSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout


    var itemsSelectedId = arrayListOf<String>()


    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSerialNumber: TextView = view.findViewById(R.id.txtSerialNumber)
        val textViewItemName: TextView = view.findViewById(R.id.txtResturantDname)
        val textViewItemPrice: TextView = view.findViewById(R.id.txtPersonDprice)
        val buttonAddToCart: Button = view.findViewById(R.id.btnAddTOCart)


    }





    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderRestaurantMenu {
        val view= LayoutInflater.from(p0.context).inflate(R.layout.recycler_description_view,p0,false)

        return ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        return restaurantMenu.size

    }

    override fun onBindViewHolder(p0: ViewHolderRestaurantMenu, p1: Int) {
        val restaurantMenuItem=restaurantMenu[p1]



        proceedToCart=proceedToCartLayout

        buttonProceedToCart.setOnClickListener(View.OnClickListener {
            getSelectedItemCount()


            val intent = Intent(context, MyCart::class.java)

            intent.putExtra("restaurantId", restaurantId)

            intent.putExtra("restaurantName", restaurantName)

            intent.putExtra("restaurantId", itemsSelectedId)

            context.startActivity(intent)
        })

            p0.buttonAddToCart.setOnClickListener(View.OnClickListener {

                if(p0.buttonAddToCart.text.toString().equals("Remove"))
                {
                    itemSelectedCount

                    itemsSelectedId.remove(p0.buttonAddToCart.tag.toString())

                    p0.buttonAddToCart.text="Add"

                    p0.buttonAddToCart.setBackgroundColor(Color.RED)

                }
                else
                {
                    itemSelectedCount++//selected

                    itemsSelectedId.add(p0.buttonAddToCart.getTag().toString())

                    p0.buttonAddToCart.text="Remove"

                    p0.buttonAddToCart.setBackgroundColor(Color.BLUE)

                }

                if(itemSelectedCount>0){
                    proceedToCart.visibility=View.VISIBLE
                }
                else{
                    proceedToCart.visibility=View.INVISIBLE
                }

            })

        p0.buttonAddToCart.tag = restaurantMenuItem.id+""
            p0.textViewSerialNumber.text=(p1+1).toString()
            p0.textViewItemName.text=restaurantMenuItem.name
            p0.textViewItemPrice.text=restaurantMenuItem.cost_for_one



    }




    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }
}




