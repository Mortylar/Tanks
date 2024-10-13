
SOURCES=$(shell find . -name "*.java")
CLASSES=$(shell find . -name "*.class")
COVERAGE_FILES=$(shell find . -name "index.html")
CLANG_CONFIG=../.clang-format

TARGET=TanksServer
ARGS=-p=1234

build:
	mvn clean package
 
run: build
	java -jar target/$(TARGET).jar

test:
	mvn clean test

coverage: test 
	open $(COVERAGE_FILES)


style:
	clang-format -style=file:$(CLANG_CONFIG) -n $(SOURCES)

fix_style:
	clang-format -style=file:$(CLANG_CONFIG) -i $(SOURCES)

open:
	vim $(shell find . -name $(file_name))

open_all_sources:
	vim $(shell find . -name "*.java")

add: fix_style
	git add $(shell find . -name "*.java")

clean:
	mvn clean


mvn_check: #do not use
	mvn checkstyle:check

a:
	java -cp ~/Downloads/hsqldb-2.7.3.jar org.hsqldb.util.DatabaseManagerSwing

b:
	java -cp ~/Downloads/hsqldb-2.7.3.jar org.hsqldb.server.Server --database.0 file:mydb --dbname.0 xdg
