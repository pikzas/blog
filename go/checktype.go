package main
import(
	"fmt"
	"reflect"
)
func main(){
	var s string = "123"
	var i int = 123
	var f float32 = 0.12
	var f2 float64 = 0.23
	var b bool = false
	fmt.Println(reflect.TypeOf(s))
	fmt.Println(reflect.TypeOf(i))
	fmt.Println(reflect.TypeOf(f))
	fmt.Println(reflect.TypeOf(f2))
	fmt.Println(reflect.TypeOf(b))
}