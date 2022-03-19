    import javax.inject.Inject;
    import java.lang.reflect.Field;
    import java.lang.reflect.InvocationTargetException;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Objects;

    public class Main {
        static class Context {
            private static Map<String, Class> types = new HashMap<>();
            private static Map<String, Object> beans = new HashMap<>();

            public static void register(String name, Class type) {
                types.put(name, type);
            }

            public static Object getBean(String name) {
                return beans.computeIfAbsent(name, key -> {
                    Class type = types.get(name);
                    Objects.requireNonNull(type, name + " not found. ");
                        try {
                            return createObject(type);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |NoSuchMethodException e) {
                        throw new RuntimeException(name + " can not instanciate");
                    }
                });
            }
            private static <T>T createObject(Class<T> type) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
                T object = type.getDeclaredConstructor().newInstance();

                for (Field field : type.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Inject.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(object, getBean(field.getName()));
                }
                return object;
            }
        }

        public static class Foo {
            String getMessage() {
                return "Hello";
            }
        }

        public static class Bar {
            @Inject
            Foo foo;

            void showMessage() {
                System.out.println(foo.getMessage());
            }
        }

        public static void main(String[] args) {
            Context.register("Foo", Foo.class);
            Context.register("Bar", Bar.class);

            Bar bar = (Bar) Context.getBean("Bar");
            bar.showMessage();

        }
    }
