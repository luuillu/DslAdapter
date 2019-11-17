# DslAdapter
A DSL style RecyclerView Adapter in Kotlin.

开发Android程序时，经常需要实现列表，常见的列表控件是RecyclerView.使用Recylerview实现一个列表通常需要写一个item 对应的xml文件，实现一个Adapter和一个ViewHolder.
这个库的作用是，把这三部分合并了，只写一个函数就能实现列表的Adapter。

这个库使用DSl方式创建布局的方式与Anko类似,但实现方式不同。Anko创建Dsl会对原有View封装了一层，这导致两个问题：
1. 封装后的View方法、属性与原有View有差异，会增加学习成本，
2. 自定义View需要写额外的的代码适配Anko
这个库用泛型实现Dsl，不需对已存在的View进行额外的包装，可以直接使用，从而解决了这个问题。

## 例1：一个包含两列数据的列表：

```
/*
* Adapter所关联的数据类型由DslAdapter的泛型参数决定。
*/
val adapter = DslAdapter<Pair<String, Int>>().also {
            /*
           * 创建ViewHolder对应的布局， 泛型反射表示要需要创建的View的类型
           * 后面紧跟的lambda表达式是一个扩展函数， receive type是泛型参数代表的View类型。
           * 所以可以在lambda表达式中对创建出来的View做一些初始化操作。
           */
            it<LinearLayout> {

                /*
                * 此处的it代表DslBindableInflater对象，它是一个辅助创建布局的工具类。
                * 主要有三个功能:
                *
                * 1.it<ViewType>{} ： 这个方法建一个childView，然后执行后面的lambda表达式初始化child，
                * 最后把child添加到自己的view中。
                *
                * 2.调用it.layoutParams{} 可以方便设置layoutParams,每个ViewGroup对应不同类型的LayoutParam，
                * 调用这个方法可以根据上下文关系，自动推导出正确的LayoutParam类型。
                *
                * 3.it.onBind{} 这个方法在Adapter执行onBind 的时候被调用，
                * 可以这个方法中得到这个item关联的数据，然后把数据展示出来
                */
                it.layoutParams {
                    width = MATCH_PARENT
                }

                orientation = HORIZONTAL

                //创建一个TextView类型的child对象，初始化之后会LinearLayout中
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

## 例2：在xml中布局
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
## 例3：MutitypeAdapter
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

## 例四：列表项预留位置(DslPlaceHolder)
有时列表需要显示多种类型的数据，这些数据来自不同的服务器接口，如果等服务返回数据后再把数据插入列表，将会导致先返回的数据显示在前面，后返回的数据显示在后面。而实际通常的需求是，数据展示顺序由业务逻辑决定，不受网络返回快慢影响。这个库中的DslPlaceHolder可以方便处理这种情况。实现原理时预先创建出DslPlaceHolder(null) 插入列表，以便占好位置，等数据返回的时候通过DslPlaceHolder更新adapter。

对于暂时没有的数据，UI上面通常有两种显示方式：1 隐藏相关项；2把相关项显示出来，但里面显示“暂无数据”。这个库支持这两种方式。既可以隐藏没有数据的项，也可以处理成显示暂无数据。详细用法可以参考工程中的Example5

