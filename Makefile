target:
	mkdir bin
	javac -classpath bin -d bin -sourcepath src src/*java

run:
	java -classpath bin SubC compile -i hello.c

clean:
	rm -rf *.class
	rm -r bin
