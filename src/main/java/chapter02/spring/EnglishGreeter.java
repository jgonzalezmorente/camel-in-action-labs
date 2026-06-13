package chapter02.spring;

public class EnglishGreeter implements Greeter {

    @Override
    public String sayHello() {
        return "Hello " + System.getProperty("user.name");
    }
}
