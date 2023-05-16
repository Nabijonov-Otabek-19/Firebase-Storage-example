package uz.gita.firebasestorageexample.presentation.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import uz.gita.firebasestorageexample.databinding.ItemImageBinding

class MyAdapter : Adapter<MyAdapter.ItemHolder>() {

    private var list: List<Uri> = ArrayList()

    fun setData(l: List<Uri>) {
        list = l
        notifyDataSetChanged()
    }

    private var longClick: ((String) -> Unit)? = null

    fun setLongClickListener(block: (String) -> Unit) {
        longClick = block
    }

    inner class ItemHolder(private val binding: ItemImageBinding) :
        ViewHolder(binding.root) {

        init {
            binding.root.setOnLongClickListener {
                longClick?.invoke(list[adapterPosition].toString())
                return@setOnLongClickListener true
            }
        }

        fun bind() {
            Glide.with(binding.root.context).load(list[adapterPosition]).into(binding.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind()
    }
}