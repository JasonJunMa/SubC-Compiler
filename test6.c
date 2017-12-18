int foo(int i){
    return i*5;
}

void bar(){
    writeln("2");
}
int main(){
    int i;
    i = 4;
    i = foo(i);
    writeln(i);
    bar();
    int j;
    j=3;
    j =foo(j);
    writeln(j);

    return 0;
}
