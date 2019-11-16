package com.lxw.dsladapter.utils

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

/**
 * Created by lxw on 2018/11/3.
 * 以DSL的方式生成Layout, 便于创建列表表头和Adapter
 */


open class DslBasicInflater<PARENT : View>(
    val view: View,
    var setLayoutParams: () -> Unit = EMPTY_LAMBDA
)

private val EMPTY_LAMBDA = {}

open class DslInflater<CURRENT : View, PARENT : View>(view: CURRENT) :
    DslBasicInflater<PARENT>(view) {
    inline operator fun <reified CHILD : View> invoke(@LayoutRes resId: Int = 0, init: CHILD.(DslInflater<CHILD, CURRENT>) -> Unit) {

        if (view !is ViewGroup) {
            throw UnsupportedOperationException("Can not add ${CHILD::class.java.simpleName} to ${view.javaClass.simpleName}, which is not ViewGroup")
        }

        val child = newViewInstance<CHILD>(view, resId)
        val childInflater = DslInflater<CHILD, CURRENT>(child)
        child.init(childInflater)
        view.addView(child)
        childInflater.setLayoutParams()
    }
}

class DslDefaultInflater<CURRENT : View>(view: CURRENT) : DslInflater<CURRENT, ViewGroup>(view)

class DslBindableInflater<BindType, CURRENT : View, PARENT : View>(
    view: CURRENT,
    val bindEvents: MutableList<(BindType, Int) -> Unit>
) : DslBasicInflater<PARENT>(view) {
    inline operator fun <reified CHILD : View> invoke(@LayoutRes resId: Int = 0, init: CHILD.(DslBindableInflater<BindType, CHILD, CURRENT>) -> Unit) {
        if (view !is ViewGroup) {
            throw UnsupportedOperationException("${view.javaClass.simpleName} is not ViewGroup")
        }
        val child = newViewInstance<CHILD>(view, resId)
        val childInflater = DslBindableInflater<BindType, CHILD, CURRENT>(child, bindEvents)
        child.init(childInflater)
        view.addView(child)
        childInflater.setLayoutParams()
    }

    fun onBind(block: (BindType) -> Unit) {
        bindEvents.add { data, _ ->
            block(data)
        }
    }

    fun onBindIndexed(block: (BindType, Int) -> Unit) {
        bindEvents.add(block)
    }

    fun bind(data: BindType, index: Int) {
        bindEvents.forEach { it(data, index) }
    }
}

class DslViewHolder<BindType>(val dslInflater: DslBindableInflater<BindType, *, *>) :
    RecyclerView.ViewHolder(dslInflater.view)

inline fun <reified V : View> newViewInstance(parent: ViewGroup, @LayoutRes resId: Int): V {
    return if (resId == 0) {
        V::class.java.getConstructor(
            Context::class.java
        ).newInstance(parent.context)
    } else {
        LayoutInflater.from(parent.context).inflate(resId, parent, false) as V
    }
}

@DslMarker
annotation class DslInflaterMarker

fun View.dip(dp: Number): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), metrics).toInt()
}

/*
*根据parent类型和layoutParams的类型设置layoutParams
**/
@Suppress("UNCHECKED_CAST")
inline fun <PARENT : ViewGroup, LAYOUT_PARAM : ViewGroup.LayoutParams> dslProcessLayoutParams(
    dlsBasicInflater: DslBasicInflater<PARENT>,
    crossinline block: LAYOUT_PARAM.() -> Unit
) {
    dlsBasicInflater.setLayoutParams =
        { (dlsBasicInflater.view.layoutParams as LAYOUT_PARAM).block() }
}

/*
*利用重载把View类型和相应的LayoutParams的类型关联起来
**/
inline fun DslBasicInflater<ViewGroup>.layoutParams(
    ignore: ViewGroup? = null,
    crossinline block: ViewGroup.MarginLayoutParams.() -> Unit
) = dslProcessLayoutParams(this, block)

inline fun <T : RecyclerView> DslBasicInflater<T>.layoutParams(
    ignore: T? = null,
    crossinline block: RecyclerView.LayoutParams.() -> Unit
) = dslProcessLayoutParams(this, block)

inline fun <T : LinearLayout> DslBasicInflater<T>.layoutParams(
    ignore: T? = null,
    crossinline block: LinearLayout.LayoutParams.() -> Unit
) = dslProcessLayoutParams(this, block)

inline fun <T : RelativeLayout> DslBasicInflater<T>.layoutParams(
    ignore: T? = null,
    crossinline block: RelativeLayout.LayoutParams.() -> Unit
) = dslProcessLayoutParams(this, block)

inline fun <T : FrameLayout> DslBasicInflater<T>.layoutParams(
    ignore: T? = null,
    crossinline block: FrameLayout.LayoutParams.() -> Unit
) = dslProcessLayoutParams(this, block)


