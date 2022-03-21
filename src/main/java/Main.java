import Entity.Bar;
import Entity.Foo;

public class Main {
    public static void main(String[] args) {
//        Context.register("foo", Foo.class);
//        Context.register("bar", Bar.class);
        Context.autoRegister();
        Bar bar = (Bar) Context.getBean("Bar");
        bar.showMessage();
    }
}
