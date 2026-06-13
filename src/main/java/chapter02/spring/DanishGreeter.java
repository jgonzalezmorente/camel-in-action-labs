package chapter02.spring;

public class DanishGreeter implements Greeter {
    @Override
    public String sayHello() {
        return "Davies " + System.getProperty("user.name");
    }
}
