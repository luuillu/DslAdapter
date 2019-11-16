# DslAdapter
A DSL style RecyclerView Adapter in Kotlin.

开发Android程序时，经常需要实现列表，常见的列表控件是RecyclerView.使用Recylerview实现一个列表通常需要写一个item 对应的xml文件，实现一个Adapter和一个ViewHolder.
这个库的作用是，把这三部分合并了，只写一个函数就能实现列表的Adapter。

例1：一个包含两列数据的列表：

```
val adapter = DslAdapter<Pair<String, Int>>().also {

            it<LinearLayout> {
                it.layoutParams {
                    width = MATCH_PARENT
                }
                orientation = HORIZONTAL
            
                it<TextView> {
                    it.layoutParams {
                        width = 0
                        weight = 1f
                    }
                    it.onBind {
                        text = it.first
                    }
                }

                it<TextView> {
                    it.layoutParams {
                        width = 0
                        weight = 1f
                    }
                    it.onBind {
                        text = it.second.toString()
                    }
                }
            }
        }
```
