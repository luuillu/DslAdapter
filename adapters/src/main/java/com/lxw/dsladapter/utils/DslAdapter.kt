package com.lxw.dsladapter.utils

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by lxw on 2018/12/7.
 * 以DSL的方式生成Adapter
 */
@DslInflaterMarker
class DslAdapter<BindType>(collection: Collection<BindType>? = null) : RecyclerView.Adapter<DslViewHolder<BindType>>(),
    MutableList<BindType> by ArrayList<BindType>() {

    init {
        if (collection != null) {
            addAll(collection)
        }
    }

    inline operator fun <reified VC : View> invoke(
        @LayoutRes resId: Int = 0,
        crossinline init: VC.(DslBindableInflater<BindType, VC, RecyclerView>) -> Unit
    ) {
        onInvoked {
            val v = newViewInstance<VC>(it, resId)
            val inflater = DslBindableInflater<BindType, VC, RecyclerView>(v, ArrayList())
            v.init(inflater)
            if (v.layoutParams == null) {
                v.layoutParams = (it as RecyclerView).layoutManager!!.generateDefaultLayoutParams()
            }

            inflater.setLayoutParams()
            return@onInvoked inflater
        }
    }

    lateinit var createInflater: (ViewGroup) -> DslBindableInflater<BindType, *, *>

    fun onInvoked(creator: (ViewGroup) -> DslBindableInflater<BindType,*, *>) {
        createInflater = creator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DslViewHolder<BindType> = DslViewHolder(
        createInflater(parent)
    )

    override fun getItemCount() = size

    override fun onBindViewHolder(holder: DslViewHolder<BindType>, position: Int) {
        holder.dslInflater.bind(elementAt(position), position)
    }
}