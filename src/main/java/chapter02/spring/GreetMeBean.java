package chapter02.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GreetMeBean {
    private Greeter greeter;

    public void setGreeter(Greeter greeter) {
        this.greeter = greeter;
    }

    public void execute() {
        System.out.println(this.greeter.sayHello());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

        GreetMeBean bean = (GreetMeBean) context.getBean("greetMeBean");
        bean.execute();
        context.close();

    }
}
