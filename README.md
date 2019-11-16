# DslAdapter
A DSL style RecyclerView Adapter in Kotlin.

开发Android程序时，经常需要实现列表，常见的列表控件是RecyclerView.使用Recylerview实现一个列表通常需要写一个item 对应的xml文件，实现一个Adapter和一个ViewHolder.
这个库的作用是，把这三部分合并了，只写一个函数就能实现列表的Adapter。

## 例1：一个包含两列数据的列表：

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
上面的代码会创建一个Adapter，Adapter中每一项的数据的类型是Pair<String, Int>。

运行后的效果为：
![](https://github.com/luuillu/DslAdapter/blob/master/image/example_1.png?raw=true)

## 例2 在xml中布局
直接在代码中创建布局存在一个确定是不支持预览，对于结构复杂的列表，可以先在XML中写好布局，然后添加到adapter中。

```
adapter = DslAdapter(dataList).also {

            it<ViewGroup>(R.layout.list_item_example_2) {

                val nameView = findViewById<TextView>(R.id.name)
                val symbolView = findViewById<TextView>(R.id.symbol)
                val priceView = findViewById<TextView>(R.id.price)
                val ratioView = findViewById<TextView>(R.id.ratio)

                it.onBind {
                    nameView.text = it.name
                    symbolView.text = it.symbol
                    priceView.text = it.price.toString()
                    ratioView.text = it.ratio.toString() + "%"
                }

            }
        }
```
## 例三 MutitypeAdapter
有时候需要在一个列表中显示不只一种类型的数据，这个库中的DslMultiTypeAdapter 实现了这个功能

```
        val adapter = DslMultiTypeAdapter().also {

            it<HeaderData, ListHeader> {
                it.layoutParams {
                    width = MATCH_PARENT
                }
            }

            it<ContentData, ViewGroup>(R.layout.list_item_example_2) {

                val nameView = findViewById<TextView>(R.id.name)
                val symbolView = findViewById<TextView>(R.id.symbol)
                val priceView = findViewById<TextView>(R.id.price)
                val ratioView = findViewById<TextView>(R.id.ratio)

                it.onBind {
                    nameView.text = it.name
                    symbolView.text = it.symbol
                    priceView.text = it.price.toString()
                    ratioView.text = it.ratio.toString() + "%"
                }

            }
        }
```
这个列表支持两种类型的数据HeaderData，和 ContentData。

## 列表项预留位置(DslPlaceHolder)
有时列表需要显示多种类型的数据，这些数据来自不同的服务器接口，如果等服务返回数据后再把数据插入列表，将会导致先返回的数据显示在前面，后返回的数据显示在后面。而实际通常的需求是，数据展示顺序由业务逻辑决定，不受网络返回快慢影响。这个库中的DslPlaceHolder可以方便处理这种情况。实现原理时预先创建出DslPlaceHolder(null) 插入列表，以便占好位置，等数据返回的时候通过DslPlaceHolder更新adapter。

对于暂时没有的数据，UI上面通常有两种显示方式：1 隐藏相关项；2把相关项显示出来，但里面显示“暂无数据”。这个库支持这两种方式。既可以隐藏没有数据的项，也可以处理成显示暂无数据。详细用法可以参考工程中的Example5

