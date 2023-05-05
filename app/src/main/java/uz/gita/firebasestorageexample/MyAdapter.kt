package uz.gita.firebasestorageexample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import uz.gita.firebasestorageexample.databinding.ItemImageBinding

class MyAdapter : Adapter<MyAdapter.ItemHolder>() {

    private var list: List<String> = ArrayList()

    fun setData(l: List<String>) {
        list = l
        notifyDataSetChanged()
    }

    inner class ItemHolder(private val binding: ItemImageBinding) :
        ViewHolder(binding.root) {

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