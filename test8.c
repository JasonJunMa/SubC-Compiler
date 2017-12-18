int foo(int i){
// do something here relate to i
// and return an integer value
    while(i<20){
        i = i + 3*i;
    }
    return i;
}
int main(){

    int i;
    int j;

    j = 1;

    i = foo(j);

    // display the value for i;
    writeln(i);

    return 0;
}
