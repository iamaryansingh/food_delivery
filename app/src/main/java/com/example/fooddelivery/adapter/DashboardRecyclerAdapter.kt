package com.example.fooddelivery.adapter
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.room.Room
import com.example.fooddelivery.R
import com.example.fooddelivery.database.RestaurantDatabase
import com.example.fooddelivery.database.RestaurantEntity
import com.example.fooddelivery.model.Restaurant
import com.example.fooddelivery.RestaurantMenuActivity
import com.squareup.picasso.Picasso
import java.util.ArrayList

class DashboardRecyclerAdapter (val context: Context, val itemList: ArrayList<Restaurant>):
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DashboardViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.recycler_dashboard_view, p0, false)

        return DashboardViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(p0: DashboardViewHolder, p1: Int) {
        val restaurant =  itemList[p1]


        val restaurantEntity=RestaurantEntity(
            restaurant.ResturantId,
            restaurant.RestaurantName

        )

        p0.btnFavourite.setOnClickListener(View.OnClickListener {
            if (!DBAsynTask(context,restaurantEntity,1).execute().get()) {

                val result=DBAsynTask(context,restaurantEntity,2).execute().get()

                if(result){

                    Toast.makeText(context,"Added to favourites",Toast.LENGTH_SHORT).show()

                    p0.btnFavourite.setTag("liked")//new value
                    p0.btnFavourite.background =
                        context.resources.getDrawable(R.drawable.ic_action_myfavourite)
                }else{

                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()

                }

            } else {

                val result=DBAsynTask(context,restaurantEntity,3).execute().get()

                if(result){

                    Toast.makeText(context,"Removed favourites",Toast.LENGTH_SHORT).show()

                    p0.btnFavourite.setTag("unliked")
                    p0.btnFavourite.background =
                        context.resources.getDrawable(R.drawable.ic_action_myfavouritefinal)
                }else{

                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()

                }

            }
        })


        p0.llcontent.setOnClickListener{

            println(p0.txtResturantkName.getTag().toString())

            val intent = Intent(context, RestaurantMenuActivity::class.java)

            intent.putExtra("restaurantId", p0.txtResturantkName.tag.toString())

            intent.putExtra("restaurantName", p0.txtResturantkName.text.toString())


            context.startActivity(intent)


        }


        p0.txtResturantkName.tag = restaurant.ResturantId
        p0.txtResturantkName.text = restaurant.RestaurantName
        p0.txtResturantPrice.text = restaurant.RestaurantPrice
        p0.txtRestaurantRating.text = restaurant.RestaurantRating
        Picasso.get().load(restaurant.RestaurantImage).error(R.drawable.dinner_and_plate)
            .into(p0.imgResturantimage)



        val checkFav=DBAsynTask(context,restaurantEntity,1).execute()
        val isFav=checkFav.get()

        if(isFav){
            p0.btnFavourite.setTag("liked")
            p0.btnFavourite.background =
                context.resources.getDrawable(R.drawable.ic_action_myfavourite)

        }else{
            p0.btnFavourite.setTag("unliked")
            p0.btnFavourite.background =
                context.resources.getDrawable(R.drawable.ic_action_myfavouritefinal)
        }

    }




    class DBAsynTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {


            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_Id)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else->return false

            }

        }


    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResturantkName: TextView = view.findViewById(R.id.txtResturantName)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val txtResturantPrice: TextView = view.findViewById(R.id.txtResturantPrice)
        val imgResturantimage: ImageView = view.findViewById(R.id.imgResturantimage)
        val llcontent: LinearLayout =
            view.findViewById(R.id.llcontent)
        val btnFavourite :TextView=view.findViewById(R.id.btnMyFavourite)
    }


}