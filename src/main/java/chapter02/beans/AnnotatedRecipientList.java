package chapter02.beans;

import org.apache.camel.RecipientList;
import org.apache.camel.language.XPath;

public class AnnotatedRecipientList {

    @RecipientList
    public String[] route(@XPath("/order/@customer") String customer) {
        if (this.isGoldCustomer(customer)) {
            return new String[] {"jms:accounting", "jms:production"};
        } else {
            return new String[] {"jms:accounting"};
        }
    }

    private boolean isGoldCustomer(String customer) {
        return customer.equals("honda");
    }
}
