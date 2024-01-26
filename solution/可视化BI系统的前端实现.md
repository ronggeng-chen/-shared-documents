# 可视化BI系统的前端实现

> [可视化BI系统的前端实现 (juejin.cn)](https://juejin.cn/post/6919086413044514824)
>
> https://www.metabase.com/docs/latest/getting-started.html

### 一、BI可视化大屏常用技术

> 本人第一次接触到可视化开发，因此在开发过程中调研和体验了很多主流的可视化开源方案，并选择了其中一些应用到我们项目中，在此和大家分享一下

#### 1. 项目主要功能

通过对产品需求进行分析，发现我们项目的可视化的需求主要包含：可视化图表展示；图表支持拖动，缩放；富文本编辑；Json编辑展示编辑器；截图；画布布局这些方面需求。核心功能图如下：

![img](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8efd71f37d4646c3bff78b5608225224~tplv-k3u1fbpfcp-watermark.image)

我们项目中需要实现的这些功能，也是可视化开发中最常用的一些功能，所以在此和大家分享一下比较好用的开源工具。

![img](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2e04cf2de76048d4bfbb05bf2ebf84f5~tplv-k3u1fbpfcp-watermark.image)

##### 1.1 Echart

> Apache EChartsTM 是一个 Apache Software Foundation (ASF) 的顶级项目。它支持丰富的图表类型，拥有千万级数据可视化渲染能力和活跃的社区。[开发文档](https://echarts.apache.org/zh/index.html)

在选择可视化库的时候，通过Ant Design Pro使用过Antv的G2Plot，然后遇到比较多的坑，很多还找不到解决方案就放弃了，不得不说，G2Plot的社区还是和Echart有较大的差距的。

##### 1.2 html2canvas

> html2canvas是一款截图插件，它可以轻松地帮你将HTML代码转换成Canvas，进而生成可保存分享的图片。

[html2canvas参考文档](http://html2canvas.hertzen.com/)

##### 1.3 react-grid-layout

> 一个用来做可视化图表的拖动(缩放)的，非常好用的 React 库，我们项目中用来做看板内图表的拖动/缩放布局的，只需要简单的配置，就能实现效果。

[参考文档](https://www.npmjs.com/package/react-grid-layout)

##### 1.4 ali-react-table

> 来自阿里的一款高性能 React 表格组件，支持交叉表和透视图，内置虚拟滚动，数据量较大时自动开启，渲染非常流畅，作者非常nice，issue基本都是立即回复，强烈推荐。（ps：就是文档写的有点草率）

[github](https://github.com/alibaba/ali-react-table)
[文档](https://ali-react-table.js.org/docs/pivot/pivot-utils/)

##### 1.5 codemirror/react-codemirror2

> Codemirror是一个在线代码编辑器工具，能够实时在线代码高亮显示，数据大屏开发中，可用于做数据源的管理，sql的输入编辑器等。

[文档](https://codemirror.net/)

##### 1.6 react-dnd

> react-dnd是React和Redux核心作者 Dan Abramov创造的一组React 高阶组件，可以在保持组件分离的前提下帮助构建复杂的拖放接口。

[文档](https://github.com/react-dnd/react-dnd)

##### 1.7

Idlize

[文档](https://www.npmjs.com/package/idlize)

[「译」代码优化策略 — Idle Until Urgent](https://juejin.cn/post/6844903686536183822)

##### 1.8 jsoneditor/jsoneditor-react

> 非常好用的json编辑器

[文档](https://github.com/josdejong/jsoneditor)

##### 1.9 puppeteer

> Puppeteer(中文翻译”操纵木偶的人”) 是 Google Chrome 团队官方的无界面（Headless）Chrome 工具，它是一个 Node 库，提供了一个高级的 API 来控制 DevTools协议上的无头版 Chrome。

[中文文档](https://zhaoqize.github.io/puppeteer-api-zh_CN/#/)

### 二、v1.0版本的前端架构和缺陷

#### 1. react架构

之前项目都是使用的vue，并且存在很多问题，比如没有对开发规范有限制；打包，发布操作缺少自动化，项目内部代码没有很好的分层等等。新项目我们需要重新规划下前端架构并且从vue迁移到react。

![img](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0a6e657ba3864780abc3a178eea4b955~tplv-k3u1fbpfcp-watermark.image)

大概画了一下我们的项目架构，因为我们的资源有限，尽量会利用已有的资源去构建。整个项目建立在react官方脚手架基础之上，并引入了代码规范，格式校验，commit-log提交规范，自动化发布，本地调试线上接口方便问题定位等内容，并优化了代码分层。

#### 2. 主要的功能模块

![img](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e5907a8a738f4152be7628abc590bd37~tplv-k3u1fbpfcp-watermark.image)

主要的功能模块有三大块，分别是多维分析页面，数据集页面，配置页面，其中最复杂的是配置页面，包含了echart各种图表的配置，单个图表的配置，指标（维度）的的配置等等。多维分析页面完全可复用配置页面的图表预览区域。

#### 3. 缺陷

![img](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/35d3e1ec7ae844969a38003135fee662~tplv-k3u1fbpfcp-watermark.image)

- 公用组件设计缺失或者设计的非常不好

  1.0版本工期非常非常紧（同事一个人一个月需要完成1.0版本的上线），因此整个项目没有很好的去设计公用组件，比如对于每个图表的配置面板上的指标/维度/过滤器/时间筛选等组件（图表预览区的多维分析部分），要不没有抽取公共组件，要不公共组件设计的非常不好，因为没有时间。。。

- 前端缺少错误监控

  缺少对导致页面崩溃的错误进行监控报警

- 前端缺少性能监控

  缺少对页面加载进性能的监控

### 三、v2.0版本的前端架构升级和完善

在1.0版本中，由于开发工期的问题，组件设计，前端监控等方面都有很大缺陷，但是由于1.0版本只支持指标卡/表格/折线图这三种图表，不支持自定义纬度，指标，排序等一系列功能，这些缺陷尚没有表现出很大的影响。

2.0版本迭代过程中，需要新增多种图表类型，对维度和指标增加了排序，聚合，自定义等一系列功能，如果不对1.0版本的缺陷修复，那会导致我们的代码极度膨胀，并且难以维护；没有错误监控报警，对于上线的bug我们也很难主动定位。因此在版本的迭代过程中我们对这些缺陷进行了一一修复。

#### 1. 公用组件设计

第一步，我们需要对配置面板中的指标，维度，时间筛选，过滤器等进行公用组件设计组件，指标和维度组件大概如下：

![img](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/db0a3334e9a446dba8ed1a6d11010546~tplv-k3u1fbpfcp-watermark.image)

![img](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5825d5a26df94a4991a64f2d71a05dc4~tplv-k3u1fbpfcp-watermark.image)

react的组件在设计的时候需要尽量设计平滑的props，不包含副作用，props尽量多给出默认值，职责单一等原则来设计，比如说这里的维度和指标都会支持一个列表，根据开发进度，在不同的图表中可能仅展示部分配置，比如图例，是属于维度，并且只能设置显示名称，所以我们可以设计一个listConfig来作为props来表示需要展示哪些部分。

根据不同的功能，设计合理的react公共组件，升级我们的架构。 ![img](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c62a63eb352a405bb569bbc99f353df2~tplv-k3u1fbpfcp-watermark.image)

#### 2. 前端监控

对项目增加错误和性能监控是保证项目平稳运行的一个重要手段

##### 2.1 错误监控

错误监控主要从下面四个方面进行

- 利用react提供的边界错误捕获方法componentDidCatch
- 采用sourceMap定位错误源码
- 钉钉报警
- 错误提示友好界面

源码也比较简单

```import
import { sendPageErrLog } from '@/const/host'
import StackTrace from 'stacktrace-js'
import { Empty } from 'antd'
import ErrorImage from '@/static/images/cry.png'
export default class ErrorBoundary extends React.Component {
    state = { error: null, errorInfo: null }

    componentDidCatch(error: Error, info: ErrorInfo) {
        this.setState({
            hasError: true,
            error: error,
            errorInfo: info
        })

        let urls = window.location.href
        if(urls.includes('local')) return false
        let params: any = {
            errUrl: urls.split('#')[1],
            serverHost: urls.split('#')[0],
            errMessage: error.message
        }
        StackTrace.fromError(error).then((err) => {
            params = {
                ...params,
                ...err[0]
            }
            window.request.post(sendPageErrLog, params).then((res: any) => {})
        })

        /* do other thing */
    }

    handleClick = () => {
        location.reload()
    }

    render() {
        if (this.state.errorInfo) {
            return (
                <Empty
                    image={ErrorImage}
                    imageStyle={{
                        height: 60
                    }}
                    description={
                        <span>
                            数据加载失败 <a style= {{color:'#1890ff'}} href="javascript:void(0);" onClick={this.handleClick}>刷新重试</a>
                        </span>
                    }
                >
                </Empty>
            )
        }

        return this.props.children
    }
}
复制代码
```

##### 2.2 性能监控

由于没有公用的监控平台，项目里就简单增加了一些关键数据的监控，性能监控指标需要慢慢完善。

![img](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0cfd563062894a729a979c78e27363fc~tplv-k3u1fbpfcp-watermark.image)

目前性能监控主要是监控看板的加载速度和保证页面的流畅程度，主要从下面几方面进行处理：

- 使用Idlize, 闲置操作
- 计算首屏看板加载时长
- 计算整个看板的加载时长

首屏看板加载时长：首先需要计算首屏看板数量，因为看板中的图表支持缩放，拖动，所以这个比较麻烦，但是由于我们一行最多几个看板的数量是有限制的，可以通过穷举来进行计算

```useEffect(()
        let clientHeight
        let temp = document.querySelectorAll('.react-grid-item')
        let resultHeight = []
        let resultWidth = []
        let cards = 0
        if(temp.length !== 0){
            clientHeight = document.body.clientHeight - 90 // 减去tabbar 
            for(let i=0;i<temp.length;i++){
                let height = temp[i] && temp[i].getBoundingClientRect().height-20 //margin
                resultHeight.push(height)
                resultWidth.push(layoutList[i].w)
            }
            let i = 0
            for(;i<resultWidth.length;){
                if(resultWidth[i] == 12){
                    cards++
                    clientHeight = clientHeight-resultHeight[i]
                    if(clientHeight < 0) break
                    i++
                }
                if(resultWidth[i]+resultWidth[i+1] == 12){
                    cards = cards + 2
                    clientHeight = clientHeight-resultHeight[i]
                    if(clientHeight < 0) break
                    i = i+2
                }

                if(resultWidth[i]+resultWidth[i+1]+resultWidth[i+2] == 12){
                    cards = cards + 3
                    clientHeight = clientHeight-resultHeight[i]
                    if(clientHeight < 0) break
                    i = i+3
                }
            }
            
        }
        props.mobileSet.setFirstContentCard(cards)
        console.log(cards)
    },[layoutList.length])
复制代码
```

计算步骤： 1. 首先统计当前看板配置的所有图表的宽度resultWidth和高度resultHeight 2. 然后设置首屏看板数目为cards， 3. 对一行存在三个看板，2个看板，1个看板进行穷举，计算出首屏看板数量

##### 看板加载时长怎么计算？

采用主要查询接口query请求完成时间来统计看板的加载时长，我们在store设置一个apiTime数组，看板中每个图表调用query接口完成之后，push一个值到apiTime，在主页面设置当apiTime长度达到首屏看板数量的时候统计首屏图表加载时长，当apiTime长度达到全部图表数量的时候统计看板加载时长。

#### 3. 依赖配置化项目开发定制化项目

事情是这样的，上个季度，我们有一个需求是定制化开发一个流量分析平台，主要是为了收拢从广告投放到用户下单的全链路流量数据到该平台，主要的功能是实现一个流量概览页，一个新建列表页，支持用户新建（全局筛选）来定制自己的流量看板等，听起来是个很大的项目有木有，然而从评审到上线就只有半个月时间。

![img](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d21698acce92447d9e9c52a68e038851~tplv-k3u1fbpfcp-watermark.image)

因为需要在很短时间上线一个项目，我们采用的方式就是依赖已有的配置化项目开发定制化项目，首先需要去评估两者之间的差异，然后利用现有的部分去支持定制化开发，尽量减少开发工作。

举几个差异化的例子以及我们是如何处理的？

- 配置化看板无全局筛选，均是以图表为单位进行查询；而定制化看板是以多图表看板为单位进行查询 这里没有什么好的方式，接口查询从单图表查询---> promise.all(),
- 定制化存在多个配置化没有的图表 利用配置化最相似的图表的数据结构去构建新类型的图表
- 定制化存在一组指标控制聚合和不聚合双图表联动 利用配置化指标卡配置全部指标的聚合数据，利用柱形图配置单指标不聚合数据，每次切换指标，不聚合数据重新请求接口展示新数据，聚合数据通过前端样式控制该指标下的聚合数据展示。
- 概览页数据查询接口较慢 后端采用缓存和bitmap来提速

文章分类