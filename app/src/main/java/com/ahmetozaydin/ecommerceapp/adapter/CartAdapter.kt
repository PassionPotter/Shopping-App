package com.ahmetozaydin.ecommerceapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmetozaydin.ecommerceapp.R
import com.ahmetozaydin.ecommerceapp.data.Cart
import com.ahmetozaydin.ecommerceapp.data.CartDatabase
import com.ahmetozaydin.ecommerceapp.databinding.EachCartBinding
import com.ahmetozaydin.ecommerceapp.utils.downloadFromUrl
import com.ahmetozaydin.ecommerceapp.utils.placeholderProgressBar
import kotlinx.coroutines.*
import kotlin.text.Typography.dollar

class CartAdapter(
    private val cartList: ArrayList<Cart>,
    val context: Context,

) : RecyclerView.Adapter<CartAdapter.PlaceHolder>() {
    class PlaceHolder(val binding: EachCartBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceHolder {
        val inflater = LayoutInflater.from(parent.context)
        //val binding = EachCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding = DataBindingUtil.inflate<EachCartBinding>(inflater, R.layout.each_cart,parent,false)
        return PlaceHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaceHolder,
        position: Int
    ) {
        holder.binding.product = cartList[position]
        /*with(holder){
            with(cartList[position]){
                binding.imageOfProduct.downloadFromUrl(this.thumbnail, placeholderProgressBar(holder.itemView.context))
                //binding.textViewProductName.text = this.title
                (dollar + this.price.toString()).also { binding.textViewProductPrice.text = it }
            }
        }*/
        holder.binding.productQuantityMinus.setOnClickListener {
            println("minus")
            changeProductQuantity(false,holder)
        }
        holder.binding.productQuantityPlus.setOnClickListener {
            println("plus")
            changeProductQuantity(true,holder)

        }
        /*holder.binding.imageOfProduct.downloadFromUrl(cartList.get(position).thumbnail, placeholderProgressBar(holder.itemView.context))
        holder.binding.textViewProductName.text = cartList.get(position).title
        (dollar + cartList.get(position).price.toString()).also { holder.binding.textViewProductPrice.text = it } //dollar olarak kendi değişkenimi kullanmadım.*/
    }
    override fun getItemCount(): Int {
        println("SIZE "+cartList.size)
        return cartList.size ?: 0
    }

     fun deleteItem(position: Int) {
        cartList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, cartList.size)
    }
    fun getItemInfo(position: Int): Int? {
        return cartList[position].id
    }
    private fun changeProductQuantity(increaseQuantity : Boolean, holder: PlaceHolder){
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val database = CartDatabase.invoke(context)
                var quantity = holder.binding.product?.id!!.let { database.cartDao().getQuantity(it) }
                println("the amount of quantity: $quantity")
                if(increaseQuantity){
                    println("increase")
                    database.cartDao().updateQuantity(holder.binding.product?.id!!,++quantity)
                    println("new quantity : "+holder.binding.product?.id!!.let { database.cartDao().getQuantity(it) })
                }else if(!increaseQuantity && quantity>1){
                    println("decrease")
                    database.cartDao().updateQuantity(holder.binding.product?.id!!,--quantity)
                    println("new quantity : "+holder.binding.product?.id!!.let { database.cartDao().getQuantity(it) })
                }
                val price = database.cartDao().getPrice(holder.binding.product?.id!!)
                CoroutineScope(Dispatchers.Main).launch{
                    holder.binding.productQuantityEditText.setText(quantity.toString())
                    println("the edit text set to $quantity")
                    //holder.binding.textViewProductPrice.text = ""
                    holder.binding.textViewProductPrice.text = (quantity*price).toString()
                }
            }

        }
    }
    fun onQuantityTextChanged(text : CharSequence,cart:Cart,priceTextView : TextView){
        val quantity = text.toString()
        if(quantity.isNotEmpty()){
            val quantityNumber = quantity.toDouble().toInt()
        }
    }
}