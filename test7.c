void foo(int i){
// do something here relate to i
    while(i<20){
        i = i + 3*i;
        writeln(i);
    }

}
int main(){

    int j;

    j = 1;
    foo(j);

    return 0;
}
