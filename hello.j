.class public hello
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;


.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method private static foo()I


.var 0 is foo I



.line 5
	iconst_1
	istore_0

	iload_0
	ireturn

.limit locals 1
.limit stack 1
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	hello/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	hello/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is k I
.var 3 is j I


.line 9
.line 10
.line 11
	invokestatic	hello/foo()I
	istore_2
.line 12
	iload_2
	iconst_4
	iadd
	istore_3

	getstatic	hello/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 4
.limit stack 3
.end method
