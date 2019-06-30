package main
import (
	"fmt"
)
func sayhello(s string) string{
	return "hello" + s
}

func main() {
	fmt.Println(sayhello("alex"))
}