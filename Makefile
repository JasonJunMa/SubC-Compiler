target:
	make clean
	mkdir classes
	javac -classpath classes -d classes -sourcepath src src/*java

run:
	java -classpath classes SubC compile -ix hello.c

clean:
	rm -rf *.class
	rm -rf classes
