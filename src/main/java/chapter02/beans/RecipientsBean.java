package chapter02.beans;

import org.apache.camel.language.XPath;

public class RecipientsBean {

    public String[] recipients(@XPath("/order/@customer") String customer) {
        if (this.isGoldCustomeer(customer)) {
            return new String[] {"jms:accounting", "jms:production"};
        } else {
            return new String[] {"jms:accounting"};
        }
    }

    private boolean isGoldCustomeer(String customer) {
        return customer.equals("honda");
    }
}
