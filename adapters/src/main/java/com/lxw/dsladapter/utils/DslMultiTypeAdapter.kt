package com.lxw.dsladapter.utils

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

/**
 * Created by lxw on 2018/12/7.
 * 以DSL的方式生成Adapter,支持添加多种类型的数据
 */
typealias LM = (Any?.(Any?) -> Unit)

@DslInflaterMarker
open class DslMultiTypeAdapter(collection: Collection<Any>? = null) :
    RecyclerView.Adapter<DslViewHolder<Any>>(),
    MutableList<Any> by ArrayList() {

    companion object {
        const val EMPTY_DATA_TYPE = -1
        val EMPTY_LAMBDA: LM = {}
    }

    init {
        if (collection != null) {
            addAll(collection)
        }
    }

    var typeCount = 0

    data class TypeInfo(
        val type: Int,
        val acceptNull: Boolean,
        val creator: (ViewGroup) -> DslBindableInflater<*, *, *>
    )

    val classToTypeInfoMap = HashMap<Class<*>, TypeInfo>()
    val typeToTypeInfoMap = SparseArray<TypeInfo>()
    private var sortedTypeClass: Array<Class<*>>? = null

    inline operator fun <reified BindType, reified VC : View> invoke(
        @LayoutRes resId: Int = 0,
        crossinline init: (VC.(DslBindableInflater<BindType, VC, RecyclerView>) -> Unit) = {}
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

    inline fun <reified BindType> onInvoked(noinline creator: (ViewGroup) -> DslBindableInflater<BindType, *, *>) {
        val type = typeCount++
        val typeInfo = TypeInfo(type, null is BindType, creator)

        if (classToTypeInfoMap.containsKey(BindType::class.java)) {
            throw IllegalStateException("${BindType::class.java} has already added before")
        }

        classToTypeInfoMap[BindType::class.java] = typeInfo
        typeToTypeInfoMap.put(type, typeInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DslViewHolder<Any> {
        @Suppress("UNCHECKED_CAST") return if (EMPTY_DATA_TYPE == viewType) {
            DslViewHolder(
                DslBindableInflater<Any, View, RecyclerView>(
                    View(parent.context),
                    ArrayList()
                )
            )
        } else {
            DslViewHolder(
                typeToTypeInfoMap[viewType].creator(parent) as DslBindableInflater<Any, *, *>
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data = get(position)
        val clazz = if (data is DslPlaceHolder<*>) {
            data.valueClass
        } else {
            data.javaClass
        }

        val typeInfo = classToTypeInfoMap[clazz] ?: getApproximateType(clazz)

        return if (data is DslPlaceHolder<*> && data.value == null && !typeInfo.acceptNull) {
            return EMPTY_DATA_TYPE;
        } else {
            typeInfo.type
        }
    }

    //如果在已声明的类型中没有找到objectClass，那么按继承关系，从声明的类型中选择与此类型最接近的类型
    private fun getApproximateType(objectClass: Class<*>): TypeInfo {
        if (sortedTypeClass == null) {
            val classArray = classToTypeInfoMap.keys.toTypedArray()

            for (i in 0 until classArray.size - 1) {
                for (j in i + 1 until classArray.size) {
                    if (classArray[i].isAssignableFrom(classArray[j])) {
                        val temp = classArray[j]
                        classArray[j] = classArray[i];
                        classArray[i] = temp
                    }
                }
            }
            sortedTypeClass = classArray
        }
        val clazz = sortedTypeClass?.find {
            it.isAssignableFrom(objectClass)
        }

        val typeInfo = classToTypeInfoMap[clazz]!!
        classToTypeInfoMap[objectClass] = typeInfo
        return typeInfo
    }

    override fun getItemCount() = size

    override fun onBindViewHolder(holder: DslViewHolder<Any>, position: Int) {
        val data = get(position)

        @Suppress("UNCHECKED_CAST")
        if (data is DslPlaceHolder<*>) {
            (holder as DslViewHolder<Any?>).dslInflater.bind(data.value, position);
        } else {
            holder.dslInflater.bind(get(position), position)
        }
    }
}