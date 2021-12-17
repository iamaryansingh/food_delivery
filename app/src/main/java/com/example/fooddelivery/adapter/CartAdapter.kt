package com.example.fooddelivery.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.fooddelivery.R
import com.example.fooddelivery.model.CartItem

class CartAdapter(val context: Context, val cartItem:ArrayList<CartItem>):RecyclerView.Adapter<CartAdapter.ViewHolderCart>() {
    class ViewHolderCart(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderItem: TextView = view.findViewById(R.id.textViewOrderItem)
        val txtViewOrderItemPrice: TextView = view.findViewById(R.id.textViewOrderItemPrice)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderCart {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.recycler_cart_view, p0, false)

        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItem.size
    }

    override fun onBindViewHolder(p0: ViewHolderCart, p1: Int) {
        val cartItemObject = cartItem[p1]

        p0.txtOrderItem.text = cartItemObject.itemName
        p0.txtViewOrderItemPrice.text = cartItemObject.itemPrice
    }

}