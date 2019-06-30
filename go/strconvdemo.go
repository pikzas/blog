package main

import(
	"fmt"
	"strconv"
	"reflect"
)

func main() {
	var b bool = false
	fmt.Println(reflect.TypeOf(b))
	var s string = strconv.FormatBool(b)
	fmt.Println(reflect.TypeOf(s))
	z,err := strconv.ParseBool(s)
	fmt.Println(reflect.TypeOf(z))
	fmt.Println(reflect.TypeOf(err))
}