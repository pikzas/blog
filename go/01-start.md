# 1 go是编译型语言，编译时将检查错误，性能优化，编译完的代码可以跨平台运行。
# 2 go是强类型语言 所有的参数和方法都要指定参数类型
```go
func sayhello(s string) string{
	return "hello" + s
}
```
# 3 go中的基本数据类型
var s string = "xyz" 或者 var s string = ""
var i int = 123 
var f float32 = 0.012
var f float64 = 0.023
bool true false 默认布尔值为false

# 4 go中的数组 声明时必须指定<b>长度</b>和<strong>类型</strong>
var beatles [4]string
beatles[0] = "john"
beatles[1] = "paul"
beatles[2] = "ringo"
beatles[3] = "george"
# 5 通过反射检查变量类型
reflect.TypeOf(var)

# 6 类型转换
strconv 工具类可以轻松实现字符串和不同类型的转换
strconv.ParseXXX 可以将入参转换为指定的XXX类型
strconv.FormatXXX 可以将XXX类型的参数转为字符串

# 7 快捷变量声明
同种类型数据
var m,n string= "abc","xyz"
不同种类型
var(
	x int = 123
	y string = "xyz"
)

# 8 初始化与零值
初始化的变量默认都是零值
int float 都是 0 
bool 是 false
string 是 ""
go 不允许将值初始化为nil值

# 9 简短变量声明
var x type = yyyy
可以用 x:=yyy
来替代，编译器会依据后面的值来自动补全x的类型信息

# 10 变量声明原则
在go中有如下几种变量声明方式
var s string = "hello world"
var m = "hello world"
var n string
n = "hello world"
x := "hello world"

go 语言中大家默认的变量声明方式为
简短变量声明不会放在方法体外 如下所示
```go
package main
import(
	"fmt"
)

var s string = "hello world"

func main(){
	i:=123
	fmt.Println(s)
	fmt.Println(i)
}
```