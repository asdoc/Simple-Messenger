build:
	@mkdir -p build
	@cp src/messenger.java build/
	@javac build/messenger.java
	@rm build/messenger.java
clean:
	@rm build/*.class
