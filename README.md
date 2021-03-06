# Kyou

在不同的系统之间交换数据时，通常会使用“报文”作为载体。

这世上有无数种报文，定长，名值对，xml，8583…… 每个系统都会定义自己的报文，并规定别的系统与其交互时必须采用这种报文。

 * 以银行界当年最时髦的8583为例，有标准的iso8583，银联的8583，各种奇形怪状的8583……（主要的区别在于编码、LLVAR的LL部分的格式和长度、以及自定义域的格式）
 * 以现在仍然很时髦的XML为例，有像`<a>3</a>`这样的风格，也有像`<a value="3"/>`这样的风格，更有像`<field><name>a</name><value>3</value></field>`这样的风格，以及各种其它奇形怪状的风格……
 * 除了这些常见的报文，还有更多不常见的报文。这些报文大多是由某个系统自己定义的，并且规定别的系统与其交互时必须使用这种报文。随便列一些，就有定长格式报文、名值对格式的报文、基于tag的报文、基于分隔符的报文，甚至基于二维表的报文……
 * 同时，要考虑的还有字节序、对齐、补位、BCD码等各种奇形怪状的约定……

总之，几乎每个系统的开发者在向外提供服务时都会设计他自己的报文。这些报文长成什么样的都有，因为设计者要考虑很多东西，比如对报文长度的追求，对解析速度的追求，对符合行业规范的追求……

## kyou是做什么的
kyou就是帮你处理掉上面提到的这些麻烦事的。

## kyou是怎么做的
kyou认为，报文的结构、数据和最终的表示之间存在着简单的对应关系，就像html+css=页面一样。

**结构 + 数据 + 样式 = 最终报文**

kyou定义了一种灵活统一的描述报文结构的方式，将所有的报文结构归结为一棵树，将所有的报文数据归结为一张表，并使用一篇报文样式规定出报文的样式，最后在运行过程中根据报文结构将报文样式应用到报文数据中的每一项上，最终得到期望的最终报文。

## kyou为谁而设计
 * 你的项目需要考虑与多个不同系统交互，但不想编写针对每一个系统的接口程序。_——kyou可以帮你搞定这些麻烦的接口，只要你提供配置。_
 * 你的项目需要与外围系统进行交互，需要一个外围系统的挡板。_——kyou可以帮你快速搭建起这个外围系统的挡板。_
 * 在你的项目中，性能不是最重要的考虑因素。_——如果拆组包部分的性能在你的项目中非常关键，你应当手写这部分代码并尽可能地优化它，就像当年那些觉得C语言太慢而去手写汇编的人一样。当然我们已经在设计的过程中尽可能地为性能做了考虑和优化。_

## kyou的目标
kyou项目的目标是实现一套通用的、高性能的、易于学习和使用的报文拆组包类库。

---

### kyou的过去
 * 初始版本尚未开发出来

### kyou的现在
 * 初始版本仍在开发中。

### kyou的将来
 * 在初始版本中将首先实现通用组包功能。这将是0.1.0版本。
 * 在随后的一版中将实现通用拆包功能。这将是0.2.0版本。
