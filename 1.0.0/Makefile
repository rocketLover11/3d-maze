JAR=maze.jar
MAIN=net.rktlvr.maze.Main

all: jar

jar:
	mkdir -p out
	javac -d out src/net/rktlvr/maze/*.java
	cp -r resources/* out/
	echo "Main-Class: $(MAIN)" > manifest.txt
	jar cfm $(JAR) manifest.txt -C out .

run:
	java -jar $(JAR)

clean:
	rm -rf out $(JAR) manifest.txt